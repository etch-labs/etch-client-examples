#!/bin/sh
# Downloads the GraalJS Community 25.1.3 JavaScript engine (9 jars, about 65 MB)
# from Maven Central into SQLcl's lib/ext folder, so that SQLcl's `script`
# command can run JavaScript again.
#
#   ./get-graaljs.sh <sqlcl-home>      e.g. ./get-graaljs.sh ~/tools/sqlcl
#
# Each jar is checked against the SHA-256 written below before it is copied in;
# if any check fails, nothing is copied. A SQLcl upgrade replaces lib/ext, so
# run this again after upgrading.
set -eu

VERSION=25.1.3
MAVEN=https://repo1.maven.org/maven2

# group path, artifact, SHA-256 (from Maven Central's .sha256 files)
JARS="
org/graalvm/sdk       collections     ffd397fae7b5cd0ef99b1917081210cec5209c8ea391ca4557d889da638569bd
org/graalvm/shadowed  icu4j           7b393274d86e02ea177ff9f99fc5f16e347c10660ebd5ea51703b5671d864e45
org/graalvm/js        js-language     3dd3dc40b1265f09de31f4e9ee443d10d93933e800162f7d4d88b3ce6e8a3f3c
org/graalvm/js        js-scriptengine b5a94e74ed15562323ca158481681325fa3f4b059483e22b6d693ffc857be3e0
org/graalvm/sdk       nativeimage     dc0516fb3007c33ebf0b7c8b654c4a5a8230e974c4b5f98bc67cbbfac725c40a
org/graalvm/polyglot  polyglot        9ff7efeded9107f72d656647c4a775dcb9fe53ed1a27a0a1bb8cee14776cb1c5
org/graalvm/regex     regex           be3532482dd3336e371d7ab93a2d8444215d3e35910b663c4d5703b978c1b195
org/graalvm/truffle   truffle-api     6514bf2d6ab58a89b196a5afa30c864eda6a96bb9010885d1c7098c0c9401042
org/graalvm/sdk       word            5b4aa7d7ef8d79cc22afaeab7396be832253140dd7758ff0a2ee0de853a6d57a
"

if [ $# -ne 1 ]; then
  echo "usage: $0 <sqlcl-home>   (the folder that holds bin/sql and lib/ext)" >&2
  exit 2
fi
SQLCL=$1
EXT=$SQLCL/lib/ext
if [ ! -f "$SQLCL/bin/sql" ] || [ ! -d "$EXT" ]; then
  echo "$SQLCL does not look like SQLcl: expected bin/sql and lib/ext" >&2
  exit 1
fi

if command -v sha256sum >/dev/null 2>&1; then
  sha256() { sha256sum "$1" | cut -d' ' -f1; }
else
  sha256() { shasum -a 256 "$1" | cut -d' ' -f1; }
fi

TMP=$(mktemp -d)
trap 'rm -rf "$TMP"' EXIT

while read -r group artifact expected; do
  [ -n "$group" ] || continue
  file=$artifact-$VERSION.jar
  printf '%-34s' "$file"
  curl -fsSL -o "$TMP/$file" "$MAVEN/$group/$artifact/$VERSION/$file"
  actual=$(sha256 "$TMP/$file")
  if [ "$actual" != "$expected" ]; then
    echo "SHA-256 MISMATCH" >&2
    echo "expected $expected, got $actual; nothing was copied" >&2
    exit 1
  fi
  echo "downloaded, SHA-256 checked"
done <<LIST
$JARS
LIST

cp "$TMP"/*.jar "$EXT"/
echo "installed 9 GraalJS $VERSION jars into SQLcl's lib/ext"
