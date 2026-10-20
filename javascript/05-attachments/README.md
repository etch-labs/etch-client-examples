# 05 Attachments

This example renders `templates/product-sheet.docx`, a product sheet with a picture for each
product. It sends the pictures as attachments, which travel as separate parts of the request instead
of as base64 text inside the JSON. That keeps the request smaller when there are many images or
large ones.

`data/products.json` names each product's picture file in `images/`. The example adds each file with
`Attachments` and `addFile`, which returns a key, and puts the key in the product's `photo` field.
The template's `{{#image photo width="72"}}` then finds the picture by that key. Adding the same
picture twice gives the same key, so the picture is sent only once.

Attachments work with a template sent from your machine, not with one stored on the ETCH server.

The code is in [index.mjs](index.mjs).

## Run

From the `javascript` folder, after following [INSTALL.md](../INSTALL.md):

```bash
node 05-attachments/index.mjs
```

The example writes the document to `05-attachments/output/`.
