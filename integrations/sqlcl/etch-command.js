// The SQLcl `etch` command: render an ETCH template from a SQL query.
//
//   SQL> script /path/to/integrations/sqlcl/etch-command.js     (once, or from login.sql)
//   SQL> etch <template> <output> '<query>'
//   SQL> etch <template> <output> @file.sql
//
// SQLcl runs `script` files in GraalJS with Nashorn compatibility switched on,
// which limits them to ECMAScript 5: no promises, async functions, classes or
// arrow functions. The ETCH JavaScript client needs more than that, so this file
// stays in ES5 and only registers the command. The work is done by etch-core.js,
// which runs in a second GraalJS context set to ES2022.

// The folder holding etch-core.js, web-shims.js and node_modules: $ETCH_SQLCL_HOME
// if set, otherwise the folder this script was loaded from (SQLcl passes the
// script's path as args[0]).
var ETCH_SQLCL_HOME = (function () {
  var JFile = Java.type("java.io.File");
  var env = java.lang.System.getenv("ETCH_SQLCL_HOME");
  if (env) return String(new JFile(env).getAbsolutePath());
  return String(new JFile(String(args[0])).getAbsoluteFile().getParent());
})();

var etchRunner = null;
// Keep the inner Context itself reachable: GraalJS 25.1 fails every render after
// the first ("API object must not be garbage collected...") if it is collected.
var etchCtx = null;

function etchCore() {
  if (etchRunner === null) {
    var Context = Java.type("org.graalvm.polyglot.Context");
    var Source = Java.type("org.graalvm.polyglot.Source");
    var JFile = Java.type("java.io.File");
    var c = Context.newBuilder("js")
      .allowAllAccess(true)
      .option("js.ecmascript-version", "2022")
      .option("engine.WarnInterpreterOnly", "false")
      .build();
    c.getBindings("js").ETCH_SQLCL_HOME = ETCH_SQLCL_HOME;
    c.eval(Source.newBuilder("js", new JFile(ETCH_SQLCL_HOME + "/etch-core.js")).build());
    etchCtx = c;
    etchRunner = c.getBindings("js").etchRun; // arrives as a callable foreign function
  }
  return etchRunner;
}

// Register the command the way SQLcl custom commands always have: a
// CommandListener that claims any statement starting with the word "etch".
var CommandRegistry = Java.type("oracle.dbtools.raptor.newscriptrunner.CommandRegistry");
var CommandListener = Java.type("oracle.dbtools.raptor.newscriptrunner.CommandListener");
var EtchCommand = Java.extend(CommandListener, {
  handleEvent: function (conn, ctx, cmd) {
    var sql = String(cmd.getSql()).trim();
    if (!/^etch(\s|$)/i.test(sql)) return false;
    try {
      etchCore()(conn, ctx, sql.substring(4));
    } catch (e) {
      ctx.write("etch: " + e + "\n");
    }
    return true;
  },
  beginEvent: function (conn, ctx, cmd) {},
  endEvent: function (conn, ctx, cmd) {}
});
// Create one instance now, while this script's context is current; GraalJS
// otherwise fails later with "No current context is available".
new EtchCommand();
CommandRegistry.addForAllStmtsListener(EtchCommand["class"]);
