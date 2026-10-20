# 04 Images

This example adds a logo to the invoice data and renders `templates/invoice-with-logo.docx`, which
places it with `{{#image logo width="56"}}`. An image goes in the data as a `data:` URI.
`image_from_file` builds one from a file on disk, and `image_from_bytes` does the same for image
bytes you already have in memory.

The code is in [main.py](main.py).

## Run

From the `python` folder, after following [INSTALL.md](../INSTALL.md):

```bash
uv run 04-images/main.py
```

The example writes the document to `04-images/output/`.
