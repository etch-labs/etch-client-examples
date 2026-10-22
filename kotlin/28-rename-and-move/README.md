# 28 Rename and move

This example renames a stored template (example 18) and moves it to another folder (example 26),
without uploading it again. Its tags and creation date stay as they were.

| Call | What it does |
|---|---|
| `renameTemplate(id, name)` | Give a stored template a new name. The name must keep the extension: a `.docx` template stays `.docx` |
| `moveTemplate(id, folder)` | Move a stored template into another folder, which must already exist |

Both return the template as it is now, with its name, its folder and its id. Use the id from the
result for any later call: a server that keeps its templates on shared storage gives a renamed
template a new id.

The example creates `example-rename` and `example-rename/archive` inside it, and uploads
`templates/invoice.docx` into `example-rename` as `invoice-draft.docx`. It renames the template to
`invoice.docx`, moves it into `example-rename/archive`, and renders it by its new path,
`example-rename/archive/invoice.docx`. The invoice is the same one the quick start makes.

It then tries two changes ETCH refuses, and prints the error's code and HTTP status:

| Change | Answer |
|---|---|
| Rename `invoice.docx` to `invoice.pdf` | `INVALID_REQUEST` (HTTP 400): a template keeps its extension |
| Move it to `example-rename/drafts`, which does not exist | `NOT_FOUND` (HTTP 404): create the folder first |

ETCH also refuses a name that another template in the same folder already has, with
`TEMPLATE_EXISTS` (HTTP 409). The example catches `ETCHException`, the base type of every exception
the client throws, and reads its `code` and `httpStatus`.

At the end it deletes the template and both folders. If an earlier run was interrupted, it starts by
removing whatever that run left under `example-rename`, deepest folder first.

To learn more about the client, see Client libraries in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [RenameAndMove.kt](RenameAndMove.kt).

## Run

From the `kotlin` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=28-rename-and-move
```

The example writes the invoice to `28-rename-and-move/output/`.
