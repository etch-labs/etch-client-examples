# 02 Data built in code

This example builds the invoice data in code instead of reading it from a file. The line items come
from a list, the subtotal, tax and total are calculated from them, and the dates are based on today.
The template is `templates/invoice.docx`, as in the quick start.

The code is in [main.py](main.py).

## Run

From the `python` folder, after following [INSTALL.md](../INSTALL.md):

```bash
uv run 02-data-built-in-code/main.py
```

The example writes the document to `02-data-built-in-code/output/`.
