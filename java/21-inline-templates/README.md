# 21 Inline templates

Every earlier example reads its template from a file. A template can also be text in the program,
held here in a text block. It is an order confirmation written in Markdown, the kind of text an
application might send as the body of an email, filled from `data/order.json`:

```text
# Order {{order.number}}

Hello {{customer.name}},
...
{{#each shipments}}
## Parcel {{@index1}}, {{carrier}}
...
```

The template still needs a name: `Template.ofBytes("confirmation.md", bytes)` gives the name and the
text, which is sent with the request. ETCH reads the format from the name, so `confirmation.md` is
treated as Markdown and the result comes back as Markdown, which the example prints and saves.

An inline template suits short text that belongs with the code, such as a notification or an email.
For a document someone designs in Word or Excel, keep the template in a file or store it on ETCH
(see `18-stored-templates`).

To learn more about Markdown templates, see Template syntax in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [InlineTemplates.java](InlineTemplates.java).

## Run

From the `java` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=21-inline-templates
```

The example prints the confirmation and writes it to
`21-inline-templates/output/order-confirmation.md`.
