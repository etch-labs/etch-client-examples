# 04 Images

This example adds a logo to the invoice data and renders `templates/invoice-with-logo.docx`, which
places it with `{{#image logo width="56"}}`. An image goes in the data as a `data:` URI.
`imageFromFile` builds one from a file on disk, and `imageFromBytes` does the same for image bytes
you already have in memory.

The code is in [Images.kt](Images.kt).

## Run

From the `kotlin` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=04-images
```

The example writes the document to `04-images/output/`.
