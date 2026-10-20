# 02 Data built in code

This example builds the invoice data in code instead of reading it from a file. The line items come
from a list, the subtotal, tax and total are calculated from them, and the dates are based on today.
The template is `templates/invoice.docx`, as in the quick start.

The code is in [index.mjs](index.mjs).

## Run

From the `javascript` folder, after following [INSTALL.md](../INSTALL.md):

```bash
node 02-data-built-in-code/index.mjs
```

The example writes the document to `02-data-built-in-code/output/`.
