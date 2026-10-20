# 18 Stored templates

This example stores a template on ETCH once and renders it by name. Every earlier example sends the
template file with each request; a stored template lives in one place, and you can update it without
changing the code that renders it.

The example works through the whole life of a stored template:

| Call | What it does |
|---|---|
| `upload_template` | Upload a file and give it a name |
| `list_templates` | List stored templates, optionally filtered with `search` or `folder` |
| `process` | Render a stored template: pass its name with `template_source` set to `TemplateSource.SERVER` |
| `update_template` | Replace a stored template with a new file |
| `download_template` | Download the stored file |
| `delete_template` | Delete it |

It uploads `templates/invoice.docx` as `example-invoice.docx` and renders it, then replaces it with
`templates/invoice-with-logo.docx` and renders it again by the same name. The render code is the
same both times, but the second invoice has the logo. The data carries the logo from the start; the
first version of the template ignores it.

The example deletes the template at the end. A stored name must be unique, so if an earlier run was
interrupted, the example first removes the copy it left behind.

`update_template`, `download_template` and `delete_template` take the template's id, which
`upload_template` returns and `list_templates` gives for each template. The id is not the name, so
keep the id the client gives you.

To learn more about stored templates, see Client libraries in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [main.py](main.py).

## Run

From the `python` folder, after following [INSTALL.md](../INSTALL.md):

```bash
uv run 18-stored-templates/main.py
```

The example writes the two invoices and the downloaded template to `18-stored-templates/output/`.
