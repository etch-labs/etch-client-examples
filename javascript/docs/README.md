# Code from the JavaScript client page

Each file here holds the code shown on the
[JavaScript client page](https://get-etch.app/ords/r/etch_prod/etch-website/docs-clients-javascript)
of the ETCH documentation, one file per section. The page pulls in the lines between a
`// docs:start <name>` and a `// docs:end <name>` comment, so what you read on the page is code that
has run. A section can be in two parts, such as its imports and the code that uses them; the page
shows them as one block.

Install the client first (`npm install` in the `javascript` folder, as [INSTALL.md](../INSTALL.md)
describes). Then run a file from the repository root, so `templates/` and `data/` are where the page
says they are:

```bash
export ETCH_API_KEY=<your API key>
node javascript/docs/quick-start.mjs
node javascript/docs/typescript.ts      # Node 23.6 or later
```

To use another server, set `ETCH_BASE_URL`. The scripts write their output to the current folder.
