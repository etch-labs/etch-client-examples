# ETCH from the SQLcl prompt

An `etch` command for [SQLcl](https://www.oracle.com/database/sqldeveloper/technologies/sqlcl/),
Oracle's command-line tool for the database. One command at the SQL prompt runs a query and turns
its result into a Word, Excel or PowerPoint document from an ETCH template:

```text
SQL> etch templates/orders.xlsx orders.xlsx 'select * from orders order by order_id'
etch: 40 rows -> orders.xlsx (7812 bytes, 669 ms)
```

The command is written in JavaScript on the ETCH JavaScript client, `@get-etch/etch-client`, and
runs inside SQLcl with its `script` command. It uses SQLcl's own database connection, so whatever
you are connected to is what it queries.

## Requirements

- SQLcl 26.3 or later. Tested on SQLcl 26.3.0 with OpenJDK 25 on macOS, against Oracle AI Database
  26ai Free.
- Node.js 22 or later and npm, only to download the ETCH JavaScript client (step 3). The command
  itself runs in SQLcl, not in Node.
- An ETCH account: a **package token** and an **API key** from your dashboard (see the
  [repository README](../../README.md#before-you-start)).
- For the schema deck demo only: the `DBMS_DEVELOPER` package, which recent Oracle Database
  releases include (tested on Oracle AI Database 26ai Free, 23.26.1). To check yours:
  `desc dbms_developer`.

## Setup

The files in this folder are:

| File | What it is |
|---|---|
| `etch-command.js` | registers the `etch` command; this is the file SQLcl loads |
| `etch-core.js` | runs the query, builds the data and calls ETCH with the JavaScript client |
| `web-shims.js` | the web APIs the client expects (`fetch`, `FormData`, `Blob` and others), built on Java |
| `get-graaljs.sh`, `get-graaljs.ps1` | download SQLcl's JavaScript engine (step 1; the `.ps1` is for Windows) |
| `.env.example` | the shape of `.env`, which holds your package token and API key (step 2) |
| `templates/`, `sql/` | the demos |

### 1. Give SQLcl a JavaScript engine

SQLcl ships without one. It used the Nashorn engine built into Java until Java 15 removed it, so
`script` now answers "JavaScript engine not found" (or `!ScriptCommand.1!`). The engine this
command uses is GraalJS Community 25.1.3, which is nine jars (about 65 MB) on Maven Central. SQLcl
finds them in its `lib/ext` folder.

On macOS and Linux, `get-graaljs.sh` downloads the nine jars from `repo1.maven.org`, checks each
against the SHA-256 written in the script, and copies them into `lib/ext` only if all nine match.
Give it the folder SQLcl is installed in (the one holding `bin/sql`):

```bash
./get-graaljs.sh ~/tools/sqlcl
```

**A SQLcl upgrade replaces `lib/ext`, so run it again after upgrading.**

To check, start SQLcl and run a one-line script:

```text
SQL> script
  2  console.log("JavaScript works")
  3  /
JavaScript works
```

With Java 24 or later, the first `script` in a session prints a few `WARNING: sun.misc.Unsafe`
lines from the engine. They are harmless.

**On Windows**, `get-graaljs.ps1` does the same in PowerShell (Windows PowerShell 5.1 or later).
Give it the folder holding `bin\sql.exe`:

```powershell
powershell -ExecutionPolicy Bypass -File .\get-graaljs.ps1 C:\tools\sqlcl
```

Windows needs one more step. On macOS and Linux, SQLcl's `bin/sql` starts Java with two GraalJS
options; `sql.exe` on Windows doesn't, and without the first one the command fails with
`"Java" is not defined`. Set them for Java before starting SQLcl:

```powershell
$env:JAVA_TOOL_OPTIONS = "-Dpolyglot.js.nashorn-compat=true -Dpolyglot.engine.WarnInterpreterOnly=false"
```

(or `set JAVA_TOOL_OPTIONS=...` in Command Prompt, or once with `setx` for every new window). Java
then prints `Picked up JAVA_TOOL_OPTIONS: ...` when SQLcl starts, which is harmless.

Tested on Windows 11 with SQLcl 26.3 and Java 25: the script, SQLcl's `script` command, and the
`etch` command loading. A full render on Windows has not been tested yet.

To install by hand on any platform, download these nine files into SQLcl's `lib/ext` folder and
check each one's SHA-256 before using it (`shasum -a 256 <file>`, or
`certutil -hashfile <file> SHA256` on Windows). All URLs start with
`https://repo1.maven.org/maven2/`.

| File | Path under `maven2/` | SHA-256 |
|---|---|---|
| `collections-25.1.3.jar` | `org/graalvm/sdk/collections/25.1.3/` | `ffd397fae7b5cd0ef99b1917081210cec5209c8ea391ca4557d889da638569bd` |
| `icu4j-25.1.3.jar` | `org/graalvm/shadowed/icu4j/25.1.3/` | `7b393274d86e02ea177ff9f99fc5f16e347c10660ebd5ea51703b5671d864e45` |
| `js-language-25.1.3.jar` | `org/graalvm/js/js-language/25.1.3/` | `3dd3dc40b1265f09de31f4e9ee443d10d93933e800162f7d4d88b3ce6e8a3f3c` |
| `js-scriptengine-25.1.3.jar` | `org/graalvm/js/js-scriptengine/25.1.3/` | `b5a94e74ed15562323ca158481681325fa3f4b059483e22b6d693ffc857be3e0` |
| `nativeimage-25.1.3.jar` | `org/graalvm/sdk/nativeimage/25.1.3/` | `dc0516fb3007c33ebf0b7c8b654c4a5a8230e974c4b5f98bc67cbbfac725c40a` |
| `polyglot-25.1.3.jar` | `org/graalvm/polyglot/polyglot/25.1.3/` | `9ff7efeded9107f72d656647c4a775dcb9fe53ed1a27a0a1bb8cee14776cb1c5` |
| `regex-25.1.3.jar` | `org/graalvm/regex/regex/25.1.3/` | `be3532482dd3336e371d7ab93a2d8444215d3e35910b663c4d5703b978c1b195` |
| `truffle-api-25.1.3.jar` | `org/graalvm/truffle/truffle-api/25.1.3/` | `6514bf2d6ab58a89b196a5afa30c864eda6a96bb9010885d1c7098c0c9401042` |
| `word-25.1.3.jar` | `org/graalvm/sdk/word/25.1.3/` | `5b4aa7d7ef8d79cc22afaeab7396be832253140dd7758ff0a2ee0de853a6d57a` |

### 2. Put your package token and API key in `.env`

The command needs two values from your ETCH dashboard. Keep them somewhere safe: your keyring,
`~/.netrc` (`%USERPROFILE%\_netrc` on Windows), or a secret in your CI. For simplicity, this example
reads them from a `.env` file. Copy `.env.example` to `.env` and fill in both:

- `ETCH_TOKEN`: your **package token** (Integrations page, JavaScript), so npm can download the
  ETCH JavaScript client
- `ETCH_API_KEY`: an **API key** (API keys page), which the command uses to call ETCH

`.env` is in `.gitignore`, so neither goes into git, and because you never type them at the prompt
they don't end up in your shell history. Load `.env` into your environment in the terminal you start
SQLcl from.

macOS and Linux:

```bash
set -a; . ./.env; set +a
```

Windows (PowerShell):

```powershell
Get-Content .env | Where-Object { $_ -match '^[A-Z_]+=' } | ForEach-Object { $n, $v = $_ -split '=', 2; Set-Item "env:$n" $v }
```

The command calls `https://api.get-etch.app`. To use another ETCH server, also set `ETCH_BASE_URL`.
The command never prints the key.

### 3. Install the ETCH JavaScript client

The command loads the client from `node_modules` in this folder. With `.env` loaded (the `.npmrc`
here reads the token from `ETCH_TOKEN`; see also [javascript/INSTALL.md](../../javascript/INSTALL.md)),
from this folder:

```bash
npm install
```

On Windows, if PowerShell says running scripts is disabled, type `npm.cmd install` instead.

### 4. Switch the command on

SQLcl runs `login.sql` every time it starts or connects. Add one line to it:

```sql
script /full/path/to/integrations/sqlcl/etch-command.js
```

SQLcl looks for `login.sql` in the current folder, then in the folders listed in `SQLPATH`. If you
have none, create one, for example in `~/sqlcl` with `export SQLPATH=~/sqlcl` in your shell profile
(on Windows, `setx SQLPATH %USERPROFILE%\sqlcl`). On Windows the line is
`script C:\full\path\to\integrations\sqlcl\etch-command.js`.

You can also run the same `script` line by hand at the SQL prompt. It is safe to run more than once
per session.

`etch-command.js` finds `etch-core.js`, `web-shims.js` and `node_modules` in the folder it was loaded from.
To keep them somewhere else, set `ETCH_SQLCL_HOME` to that folder.

## Usage

```text
SQL> etch <template> <output> '<query>'
SQL> etch <template> <output> @file.sql
```

- Quote the query with single quotes, and double a quote inside it: `'select * from orders where
  status = ''Delivered'''`. A query in a file is easier for anything longer; a trailing `;` or `/`
  is ignored.
- Paths are relative to the folder you started SQLcl in.
- The output format is the template's (`.docx`, `.xlsx` or `.pptx`).
- The template is sent with each request, so nothing has to be stored on the ETCH server first.

### What the template receives

- **A flat query** becomes `{"rows": [ … ]}`, one object per row, with the column names as Oracle
  returns them, which is usually uppercase: `{{#each rows}}{{ORDER_NO}}{{/each}}`. Numbers are
  sent as text exactly as the database holds them, so `30.00` keeps its two decimals; ETCH's
  formatters, sums, comparisons and sorting still treat them as numbers, and Excel gets number
  cells. Dates are ISO date-times (`2026-08-05T00:00`), and everything else is text.
- **A query that returns one row with one column holding a JSON object** (built with
  `JSON_OBJECT`, `JSON_ARRAYAGG` and so on) is used as the data itself. That is how to send nested
  data, for example a sheet per customer with their orders inside.
- **A BLOB column** (a picture) is sent as a binary attachment with the client's `Attachments`,
  not as base64 text. The row holds the attachment's key, which `{{#image COLUMN}}` finds.
  Identical pictures are sent once.

## The demos

From this folder, connected to a schema with the ORDERS and PRODUCTS tables (see
[Sample tables](#sample-tables) below):

| Command | What you get |
|---|---|
| `etch templates/orders.xlsx orders.xlsx 'select * from orders order by order_id'` | an Excel sheet with a row per order, data bars on the totals and the overdue orders shaded, and a Summary sheet Excel works out from the data: one row per status (`UNIQUE`), its orders and revenue, and a chart |
| `etch templates/per-customer.xlsx per-customer.xlsx @sql/per-customer.sql` | an Excel workbook with a sheet per customer, from one JSON query |
| `etch templates/products.docx products.docx 'select * from products order by product_id'` | a Word table of products, each with its photo from the IMAGE BLOB |
| `etch templates/schema.pptx schema.pptx @sql/schema.sql` | a PowerPoint deck documenting every table in your schema, from `DBMS_DEVELOPER.GET_METADATA`: an overview, then a slide per table with its columns, statistics, keys and indexes |

The templates were built by hand in Excel, Word and PowerPoint, as shown in the video. In
`orders.xlsx`, the Summary's status list is a dynamic array (`=SORT(UNIQUE(FILTER(...)))`), which
needs Excel 365 or Excel 2021 or later; the chart reads it through two names, `Statuses` and
`Revenue` (`=Summary!$A$5#` and `=Summary!$C$5#`), so it grows with the list.

### Sample tables

If your schema has no ORDERS and PRODUCTS tables, `sql/setup.sql` creates them with made-up data
(20 products, 40 orders, the data ET007 uses), and `sql/load-images.js` loads the product photos
from the `images/` folder here. The photos are CC0 or public domain; `images/CREDITS.md` lists
each one's creator and source.

```text
SQL> @sql/setup.sql
SQL> script sql/load-images.js
```

The database user needs the CREATE TABLE and CREATE SEQUENCE privileges (ORDERS has an identity
column), and quota on its default tablespace. `setup.sql` first checks that the user has both
privileges, and that the schema has no ORDERS or PRODUCTS table and none of the constraint names it
uses. If a check fails, it stops with a message and exits SQLcl without changing anything. It never
drops or alters an existing table.

`sql/teardown.sql` drops the two tables again, and only if they carry `setup.sql`'s table comment.
`setup.sql` comments each table as soon as it creates it, so `teardown.sql` can also clean up after a
run that failed part of the way through.

## Licences

SQLcl is licensed by Oracle under the Oracle Free Use Terms and Conditions. The JavaScript engine is
GraalJS Community from Maven Central, under the Universal Permissive License (UPL) 1.0; its
`js-language` jar is under the UPL and the MIT licence, and its `icu4j` jar under the Unicode/ICU
licence. Check that these licences, and ETCH's own terms, meet your organisation's requirements.
This isn't legal advice.

## Further reading

- Jeff Smith (Oracle), [Running JavaScript in Oracle SQLcl](https://www.thatjeffsmith.com/archive/2022/04/running-javascript-in-oracle-sqlcl-22-1/)
- Philipp Hartenfeller, [SQLcl with JavaScript on macOS using GraalVM](https://hartenfeller.dev/blog/sqlcl-js-graalvm-macos)
- Philipp Salvisberg, [The return of SQLcl's script command](https://www.salvis.com/blog/2026/07/25/the-return-of-sqlcls-script-command/)
