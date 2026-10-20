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
| `license_capabilities()` | The licence behind your API key: whose it is, its status, when it expires and the capabilities it grants. `has()` checks one capability, here `format.pdf`, before relying on it |

The capabilities depend on your plan, and the example prints the ones your key has; `format.pdf` is the
one that allows PDF output. Checking first lets an application hide or explain a feature the plan does
not include, rather than fail when a user tries it.

`healthcheck()` is named after the server's endpoint. Before client 26.10.9 it was `health()`.

To learn more about these calls, see Client libraries in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [main.py](main.py).

## Run

From the `python` folder, after following [INSTALL.md](../INSTALL.md):

```bash
uv run 24-server-information/main.py
```

The example prints its findings and writes no files.
