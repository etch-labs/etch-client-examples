// The working half of the SQLcl `etch` command. etch-command.js evaluates this file in
// its own GraalJS context (ES2022) and calls etchRun for each `etch` command.
//
//   etch <template> <output> '<query>'
//   etch <template> <output> @file.sql
//
// It runs the query on SQLcl's own connection, turns the result into ETCH data,
// sends the template and data to ETCH with the ETCH JavaScript client, and
// writes the document.
//
//   ETCH_API_KEY    your API key (required; never printed)
//   ETCH_BASE_URL   the ETCH server, default https://api.get-etch.app
//
// ETCH_SQLCL_HOME (this folder) is set by etch-command.js before this file runs.

const Files = Java.type("java.nio.file.Files");
const Paths = Java.type("java.nio.file.Paths");
const JString = Java.type("java.lang.String");
const StandardCharsets = Java.type("java.nio.charset.StandardCharsets");
const Types = Java.type("java.sql.Types");
const DateTimeFormatter = Java.type("java.time.format.DateTimeFormatter");
const System = Java.type("java.lang.System");

const HOME = String(ETCH_SQLCL_HOME);
const CLIENT = HOME + "/node_modules/@get-etch/etch-client/dist/index.js";

function mustExist(path, what) {
  if (!Files.isRegularFile(Paths.get(path))) throw new Error(what + " not found: " + path);
  return path;
}

function readText(path) {
  return String(new JString(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8));
}

// Load the ETCH JavaScript client. SQLcl's JavaScript is not Node, so web-shims.js
// supplies fetch, FormData, Blob and the other web APIs the client uses. The
// client's CommonJS build is then loaded with a minimal module wrapper, since it
// needs no require().
load(HOME + "/web-shims.js");

const Etch = (() => {
  if (!Files.exists(Paths.get(CLIENT))) {
    throw new Error("the ETCH JavaScript client is not installed: run npm install in " + HOME);
  }
  const module = { exports: {} };
  const factory = new Function("module", "exports", "require", readText(CLIENT));
  factory(module, module.exports, (name) => {
    throw new Error("require('" + name + "') is not available in SQLcl");
  });
  return module.exports;
})();

function env(name) {
  const v = System.getenv(name);
  return v === null || v === undefined ? "" : String(v).trim();
}

// Split the command line into words. A word can be quoted with ' or ", and a
// doubled quote inside the quotes stands for one quote character.
function parseArgs(s) {
  const out = [];
  let i = 0;
  const n = s.length;
  while (i < n) {
    while (i < n && /\s/.test(s[i])) i++;
    if (i >= n) break;
    const q = s[i];
    if (q === "'" || q === '"') {
      let buf = "";
      i++;
      while (i < n) {
        if (s[i] === q) {
          if (s[i + 1] === q) { buf += q; i += 2; continue; }
          i++;
          break;
        }
        buf += s[i++];
      }
      out.push(buf);
    } else {
      const start = i;
      while (i < n && !/\s/.test(s[i])) i++;
      out.push(s.substring(start, i));
    }
  }
  return out;
}

// Turn a query result into ETCH data. When the query returns one row with one
// column holding a JSON object, that object is the data. Otherwise the data is
// {"rows": [{COLUMN: value, ...}, ...]}, with the column names as Oracle returns
// them (normally uppercase), and the values sent like this:
//   numbers   text, exactly as stored (30.00 keeps its scale)
//   dates     ISO date-times, e.g. 2026-08-05T00:00
//   BLOBs     binary attachments; the row holds the attachment's key, which
//             {{#image COLUMN}} finds. Identical BLOBs are sent once.
//   others    text
function runQuery(conn, sql, attachments) {
  const st = conn.createStatement();
  try {
    const rs = st.executeQuery(sql);
    const md = rs.getMetaData();
    const n = md.getColumnCount();
    const rows = [];
    while (rs.next()) {
      const row = {};
      for (let c = 1; c <= n; c++) {
        const name = String(md.getColumnLabel(c));
        let v;
        switch (md.getColumnType(c)) {
          case Types.NUMERIC: case Types.DECIMAL: case Types.INTEGER: case Types.SMALLINT:
          case Types.BIGINT: case Types.FLOAT: case Types.DOUBLE: case Types.REAL: {
            const bd = rs.getBigDecimal(c);
            // Send numbers as text, exactly as stored, so 30.00 keeps its scale.
            // ETCH's formatters, sums, comparisons and sorting read numeric text as numbers.
            v = bd === null ? null : String(bd.toPlainString());
            break;
          }
          case Types.DATE: case Types.TIMESTAMP: {
            const ts = rs.getTimestamp(c);
            v = ts === null ? null : String(ts.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            break;
          }
          case Types.BLOB: case Types.LONGVARBINARY: case Types.VARBINARY: case Types.BINARY: {
            const bytes = rs.getBytes(c);
            v = bytes === null ? null : attachments.addBytes(__etchBytes.fromJava(bytes));
            break;
          }
          default: {
            const s = rs.getString(c);
            v = s === null ? null : String(s);
          }
        }
        row[name] = v;
      }
      rows.push(row);
    }
    if (rows.length === 1 && n === 1) {
      const only = rows[0][String(md.getColumnLabel(1))];
      if (typeof only === "string" && /^\s*\{/.test(only)) {
        try { return JSON.parse(only); } catch (e) { /* not JSON: use it as a row */ }
      }
    }
    return { rows };
  } finally {
    st.close();
  }
}

// Run one etch command.
function etchCommand(conn, ctx, args) {
  if (args.length !== 3) {
    ctx.write("usage: etch <template> <output> '<query>'\n       etch <template> <output> @file.sql\n");
    return;
  }
  const [template, output] = args;
  let query = args[2];
  mustExist(template, "template");
  if (query.startsWith("@")) query = readText(mustExist(query.substring(1), "query file"));
  query = query.trim().replace(/[;\/]\s*$/, "").trim();
  if (!conn) { ctx.write("etch: not connected\n"); return; }

  const apiKey = env("ETCH_API_KEY");
  if (!apiKey) { ctx.write("etch: set ETCH_API_KEY before starting SQLcl\n"); return; }

  const started = System.currentTimeMillis();
  const attachments = new Etch.Attachments();
  const data = runQuery(conn, query, attachments);
  const count = (n, one, many) => n + " " + (n === 1 ? one : many);
  const what = data.rows ? count(data.rows.length, "row", "rows") : "1 JSON document";
  const images = attachments.size;

  const client = new Etch.ETCHClient({
    baseURL: env("ETCH_BASE_URL") || "https://api.get-etch.app",
    apiKey,
    // Retries wait with setTimeout, which never fires inside a SQLcl command.
    maxRetries: 0
  });
  const options = {
    template: String(Paths.get(template).getFileName()),
    templateContent: new Blob([__etchBytes.fromJava(Files.readAllBytes(Paths.get(template)))]),
    data
  };
  if (images > 0) options.attachments = attachments;

  // fetch is synchronous underneath (web-shims.js), so this chain has settled by
  // the time SQLcl prints the next prompt.
  client.process(options)
    .then((result) => result.data.arrayBuffer())
    .then((ab) => {
      Files.write(Paths.get(output), __etchBytes.toJava(new Uint8Array(ab)));
      ctx.write("etch: " + what + (images ? ", " + count(images, "image", "images") + " attached" : "") +
        " -> " + output + " (" + ab.byteLength + " bytes, " +
        (System.currentTimeMillis() - started) + " ms)\n");
    })
    .catch((e) => {
      ctx.write("etch: failed: " + (e && e.message ? e.message : e) + (e && e.code ? " [" + e.code + "]" : "") + "\n");
    });
}

globalThis.etchRun = function (conn, ctx, line) {
  try {
    etchCommand(conn, ctx, parseArgs(String(line)));
  } catch (e) {
    ctx.write("etch: " + (e && e.message ? e.message : e) + "\n");
  }
};
