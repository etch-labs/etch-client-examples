# Downloads the GraalJS Community 25.1.3 JavaScript engine (9 jars, about 65 MB)
# from Maven Central into SQLcl's lib\ext folder, so that SQLcl's `script`
# command can run JavaScript again. It is the Windows version of get-graaljs.sh.
#
#   powershell -ExecutionPolicy Bypass -File .\get-graaljs.ps1 <sqlcl-home>
#   e.g. powershell -ExecutionPolicy Bypass -File .\get-graaljs.ps1 C:\tools\sqlcl
#
# Each jar is checked against the SHA-256 written below before it is copied in;
# if any check fails, nothing is copied. A SQLcl upgrade replaces lib\ext, so
# run this again after upgrading. Works with Windows PowerShell 5.1 and later.
param([Parameter(Mandatory = $true)][string]$SqlclHome)
$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'   # the progress bar makes Invoke-WebRequest very slow on 5.1
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$Version = '25.1.3'
$Maven = 'https://repo1.maven.org/maven2'

# group path, artifact, SHA-256 (from Maven Central's .sha256 files); the same list as get-graaljs.sh
$Jars = @(
    @('org/graalvm/sdk',      'collections',     'ffd397fae7b5cd0ef99b1917081210cec5209c8ea391ca4557d889da638569bd'),
    @('org/graalvm/shadowed', 'icu4j',           '7b393274d86e02ea177ff9f99fc5f16e347c10660ebd5ea51703b5671d864e45'),
    @('org/graalvm/js',       'js-language',     '3dd3dc40b1265f09de31f4e9ee443d10d93933e800162f7d4d88b3ce6e8a3f3c'),
    @('org/graalvm/js',       'js-scriptengine', 'b5a94e74ed15562323ca158481681325fa3f4b059483e22b6d693ffc857be3e0'),
    @('org/graalvm/sdk',      'nativeimage',     'dc0516fb3007c33ebf0b7c8b654c4a5a8230e974c4b5f98bc67cbbfac725c40a'),
    @('org/graalvm/polyglot', 'polyglot',        '9ff7efeded9107f72d656647c4a775dcb9fe53ed1a27a0a1bb8cee14776cb1c5'),
    @('org/graalvm/regex',    'regex',           'be3532482dd3336e371d7ab93a2d8444215d3e35910b663c4d5703b978c1b195'),
    @('org/graalvm/truffle',  'truffle-api',     '6514bf2d6ab58a89b196a5afa30c864eda6a96bb9010885d1c7098c0c9401042'),
    @('org/graalvm/sdk',      'word',            '5b4aa7d7ef8d79cc22afaeab7396be832253140dd7758ff0a2ee0de853a6d57a')
)

$Ext = Join-Path $SqlclHome 'lib\ext'
if (-not (Test-Path (Join-Path $SqlclHome 'bin\sql.exe')) -or -not (Test-Path $Ext)) {
    Write-Error "$SqlclHome does not look like SQLcl: expected bin\sql.exe and lib\ext"
    exit 1
}

$Tmp = Join-Path ([IO.Path]::GetTempPath()) ("graaljs-" + [Guid]::NewGuid())
New-Item -ItemType Directory -Path $Tmp | Out-Null
try {
    foreach ($j in $Jars) {
        $group, $artifact, $expected = $j
        $file = "$artifact-$Version.jar"
        Write-Host -NoNewline ($file.PadRight(34))
        Invoke-WebRequest -UseBasicParsing -Uri "$Maven/$group/$artifact/$Version/$file" -OutFile (Join-Path $Tmp $file)
        $actual = (Get-FileHash -Algorithm SHA256 (Join-Path $Tmp $file)).Hash.ToLower()
        if ($actual -ne $expected) {
            Write-Host 'SHA-256 MISMATCH'
            Write-Error "expected $expected, got $actual; nothing was copied"
            exit 1
        }
        Write-Host 'downloaded, SHA-256 checked'
    }
    Copy-Item (Join-Path $Tmp '*.jar') $Ext
    Write-Host "installed 9 GraalJS $Version jars into SQLcl's lib\ext"
    Write-Host ''
    Write-Host 'On Windows, also set this before starting SQLcl (see the README):'
    Write-Host '  $env:JAVA_TOOL_OPTIONS = "-Dpolyglot.js.nashorn-compat=true -Dpolyglot.engine.WarnInterpreterOnly=false"'
}
finally {
    Remove-Item -Recurse -Force $Tmp
}
