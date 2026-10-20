# 16 Output formats

This example renders the invoice from the quick start four ways, writing four files:

| File | How it is made |
|---|---|
| `invoice-DEMO-0001.docx` | The template is a Word document, so by default the output is too |
| `invoice-DEMO-0001.pdf` | The same template rendered straight to PDF with `output_format` |
| `invoice-DEMO-0001-protected.docx` | A Word document that asks for a password before it opens, using `password` |
| `invoice-DEMO-0001-converted.pdf` | An existing document (the Word file above) converted to PDF with `convert` |

Rendering to PDF and converting to PDF give the same result here. Use the first when you have a
template and data, and the second for a document you already have, whether ETCH made it or not.

The output formats are Word (`docx`), Excel (`xlsx`), PowerPoint (`pptx`), Markdown (`md`) and
PDF (`pdf`). When you give none, the output has the template's own format.

The password in the example is a placeholder. In a real application, take it from your
configuration rather than the code.

To learn more about output formats and conversion, see Client libraries in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [main.py](main.py).

## Run

From the `python` folder, after following [INSTALL.md](../INSTALL.md):

```bash
uv run 16-output-formats/main.py
```

The example writes the four files to `16-output-formats/output/`. PDF output needs the ETCH server
to have PDF conversion available, as it does on `api.get-etch.app`.
