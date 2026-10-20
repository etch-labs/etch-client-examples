# 01 Quick start

This example loads `data/invoice.json` and renders `templates/invoice.docx` with it. It is the
shortest complete program: create a client, render the template with the data, save the document.

The client reads your API key from `ETCH_API_KEY`, and the server address from `ETCH_BASE_URL` when you set it (the hosted API otherwise), so neither is in the code.

The same program makes an Excel workbook: change `invoice.docx` to `invoice.xlsx` in both places, and the output is a workbook with a row per item and a subtotal that grows to fit. The template decides the format.

The code is in [main.py](main.py).

## Run

From the `python` folder, after following [INSTALL.md](../INSTALL.md):

```bash
uv run 01-quick-start/main.py
```

The example writes the document to `01-quick-start/output/`.
