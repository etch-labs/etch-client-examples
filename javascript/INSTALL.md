# Installing the JavaScript examples

The examples use `@get-etch/etch-client` 26.10.12, which is published on the ETCH package registry
rather than npmjs.com. You need Node.js 22 or later and two things from your ETCH dashboard:

- your package token, from the Integrations page, so npm can download the client
- an API key, from the API keys page, which the examples use to call ETCH

## 1. Give npm your package token

The `.npmrc` in this folder sends the `@get-etch` scope to the ETCH registry and reads the token from
the `ETCH_TOKEN` environment variable, so the token never goes in a file.

macOS and Linux:

```bash
export ETCH_TOKEN=<your package token>
```

Windows (PowerShell):

```powershell
$env:ETCH_TOKEN = "<your package token>"
```

This setting lasts until you close the terminal. To keep the token, add the `export` line to your
shell profile (`~/.zshrc` or `~/.bashrc`), or on Windows run `setx ETCH_TOKEN <your package token>`
and open a new terminal.

## 2. Install

From the `javascript` folder:

```bash
npm install
```

On Windows, if PowerShell says running scripts is disabled, allow them once with
`Set-ExecutionPolicy -Scope CurrentUser RemoteSigned`, or type `npm.cmd install` instead.

## 3. Set your API key

macOS and Linux:

```bash
export ETCH_API_KEY=<your API key>
```

Windows (PowerShell):

```powershell
$env:ETCH_API_KEY = "<your API key>"
```

The examples call `https://api.get-etch.app`. To use another ETCH server, also set `ETCH_BASE_URL`.

## 4. Run an example

```bash
node 01-quick-start/index.mjs
```

The example writes the document to `01-quick-start/output/`.
