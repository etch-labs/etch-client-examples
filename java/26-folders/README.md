# 26 Folders

This example keeps a stored template (example 18) in a folder and renders it by its path. Folders
let you group templates, for example one folder per customer or per kind of document. A folder can
hold other folders, and a template's name only has to be unique within its folder, so two folders
can each have their own `invoice.docx`.

| Call | What it does |
|---|---|
| `createFolder` | Create a folder; an optional second argument is the parent folder |
| `uploadTemplate` | Upload a file; the third argument puts it in a folder |
| `listFolders` | List the folders, with the number of templates in each |
| `listTemplates` | With a folder as the first argument, list the templates in that folder |
| `process` | Render a stored template by its path, named with `Template.stored` |
| `deleteFolder` | Delete a folder by its id, which is its path |

The example creates `example-folders` and `example-folders/invoices` inside it, uploads
`templates/invoice.docx` into the inner folder, lists the folders and the templates in
`example-folders/invoices`, and renders the template by its path,
`example-folders/invoices/invoice.docx`. The invoice is the same one the quick start makes.

You can delete a folder only when it is empty, with no templates and no folders in it, so the
example deletes the template first, then the inner folder, then the outer one. If an earlier run was
interrupted, the example starts by removing whatever it left under `example-folders`, deepest folder
first.

To learn more about folders, see Client libraries in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [Folders.java](Folders.java).

## Run

From the `java` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=26-folders
```

The example writes the invoice to `26-folders/output/`.
