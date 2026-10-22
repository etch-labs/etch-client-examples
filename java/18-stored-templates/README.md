# 18 Stored templates

This example stores a template on ETCH once and renders it by name. Every earlier example sends the
template file with each request; a stored template lives in one place, and you can update it without
changing the code that renders it.

The example works through the whole life of a stored template:

| Call | What it does |
|---|---|
| `uploadTemplate` | Upload a file and give it a name |
| `listTemplates` | List stored templates, optionally filtered by folder, format or a search term |
| `process` | Render a stored template: name it with `Template.stored`, which never sends a local file of the same name |
| `updateTemplate` | Replace a stored template with a new file |
| `downloadTemplate` | Download the stored file |
| `deleteTemplate` | Delete it |

It uploads `templates/invoice.docx` as `example-invoice.docx` and renders it, then replaces it with
`templates/invoice-with-logo.docx` and renders it again by the same name. The render code is the
same both times, but the second invoice has the logo. The data carries the logo from the start; the
first version of the template ignores it.

The example deletes the template at the end. A stored name must be unique, so if an earlier run was
interrupted, the example first removes the copy it left behind.

`updateTemplate`, `downloadTemplate` and `deleteTemplate` take the template's id, which
`uploadTemplate` returns and `listTemplates` gives for each template. The id is not the name, so
keep the id the client gives you.

To learn more about stored templates, see Client libraries in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [StoredTemplates.java](StoredTemplates.java).

## Run

From the `java` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=18-stored-templates
```

The example writes the two invoices and the downloaded template to `18-stored-templates/output/`.
