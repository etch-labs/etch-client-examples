# Code from the Python client page

Each file here holds the code shown on the
[Python client page](https://get-etch.app/docs-clients-python) of the ETCH documentation, one file
per section. The page pulls in the lines between a `# docs:start <name>` and a `# docs:end <name>`
comment, so what you read on the page is code that has run.

Run a file from the repository root, so `templates/` and `data/` are where the page says they are:

```bash
export ETCH_API_KEY=<your API key>
uv run --project python python/docs/quick_start.py
```

To use another server, set `ETCH_BASE_URL`. The scripts write their output to the current folder.
