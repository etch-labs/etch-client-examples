# 21 Inline templates

This example renders a template written in the program, held in a template literal, instead of one
read from a file. It is an order confirmation written in Markdown, the kind of text an application
might send as the body of an email, filled from `data/order.json`:

```text
# Order {{order.number}}

Hello {{customer.name}},
...
{{#each shipments}}
## Parcel {{@index1}}, {{carrier}}
...
```

The template still needs a name: `template` gives the name and `templateContent` the text. ETCH
reads the format from the name: it treats `confirmation.md` as Markdown and returns the result as
Markdown, which the example prints and saves.

An inline template suits short text that belongs with the code, such as a notification or an email.
For a document someone designs in Word or Excel, keep the template in a file or store it on ETCH
(see `18-stored-templates`).

To learn more about Markdown templates, see Template syntax in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [index.mjs](index.mjs).

## Run

From the `javascript` folder, after following [INSTALL.md](../INSTALL.md):

```bash
node 21-inline-templates/index.mjs
```

The example prints the confirmation and writes it to
`21-inline-templates/output/order-confirmation.md`.
