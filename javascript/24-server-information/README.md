# 24 Server information

This example checks the server and the licence before any work starts, and prints what it found:

```text
Server: healthy, version v26.10.1
Licence: Example Client Ltd, ACTIVE, expires 2027-10-04
Capabilities: cli.access, convert.pdf, format.docx, format.pdf, ...
PDF output: allowed
```

| Call | What it tells you |
|---|---|
| `healthcheck()` | Whether the server is up. The example stops with a message if it is not, which is cheaper than finding out halfway through a long run |
| `version()` | Which version of ETCH is answering. Log it alongside your own version |
| `licenseCapabilities()` | The licence behind your API key: whose it is, its status, when it expires and the capabilities it grants. `has()` checks one capability, here `format.pdf`, before relying on it |

The capabilities depend on your plan, and the example prints the ones your key has; `format.pdf` is the
one that allows PDF output. Checking first lets an application hide or explain a feature the plan does
not include, rather than fail when a user tries it.

The methods are named after the server's endpoints. Before client 26.10.9 they were `getHealth()`, `getVersion()` and `getLicenseCapabilities()`.

To learn more about these calls, see Client libraries in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [index.mjs](index.mjs).

## Run

From the `javascript` folder, after following [INSTALL.md](../INSTALL.md):

```bash
node 24-server-information/index.mjs
```

The example prints its findings and writes no files.
