# 22 Missing values

This example shows what ETCH does when the data lacks a value the template uses. ETCH can fail the
render, leave the value out, or leave the tag in the document. The setting is `undefined_variables`,
one of `UndefinedVariables.ERROR`, `.EMPTY` and `.KEEP`. The example removes `customer.address`
from the data, as for a customer whose address is not known yet, and renders
`templates/invoice.docx` once with each setting:

| Setting | Result |
|---|---|
| error (the default) | No document. The render fails with a `ValidationError` that names the gap: `key 'address' not found in data for path 'customer.address'` |
| empty | `invoice-empty.docx`, with the address left out: its line is there, but blank |
| keep | `invoice-keep.docx`, with `{{customer.address}}` printed where the address would be |

Keep the default in production: a document with a gap in it is usually worse than no document, and
the error says exactly what is missing. `keep` is useful while designing a template, to see every
value the data does not supply yet. `empty` suits optional details; a default value in the template
(see `06-variables`) or a conditional (`08-conditionals`) says the same thing more precisely, for
one value at a time.

To learn more about missing values, see Variables under Template syntax in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [main.py](main.py).

## Run

From the `python` folder, after following [INSTALL.md](../INSTALL.md):

```bash
uv run 22-missing-values/main.py
```

The example writes two invoices to `22-missing-values/output/`. The first render makes no document,
so the example prints its error instead.
