# 17 Batch

This example renders an invoice for each of the three records in `data/invoices.json` in a single
request, then saves them as separate files and as one zip.

The template, `templates/invoice-batch.docx`, is the quick-start invoice with one addition before
the title:

```text
{{#document-each invoices filename="invoice-{{number}}.docx"}}
```

The marker tells the server to render the whole template once per element of `invoices` and to name
each file from that element. Inside each document the invoice's own fields (`number`, `customer`,
`items`) are read directly, and the shared `seller` details come from the top of the data. The
example calls `renderBatch` instead of `process` and gets back every document, which it saves as
separate files with `saveAll` and as a zip with `saveArchive`.

A record that fails (for example one missing a field the template needs) fails on its own: the
other documents are still returned, and `failures` lists each failed record with its reason. The
example prints them.

Each document in a batch counts as one render towards your plan's allowance.

To learn more about batch rendering, see Client libraries in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [index.mjs](index.mjs).

## Run

From the `javascript` folder, after following [INSTALL.md](../INSTALL.md):

```bash
node 17-batch/index.mjs
```

The example writes the invoices to `17-batch/output/invoices/`, and the same documents as a zip to
`17-batch/output/invoices.zip`.
