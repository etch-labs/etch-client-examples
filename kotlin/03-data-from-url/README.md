# 03 Data from a URL

This example downloads the invoice data from a URL, then renders `templates/invoice.docx` with it.
Any URL that returns the JSON works, for example the raw URL of `data/invoice.json` in this
repository.

The code is in [DataFromUrl.kt](DataFromUrl.kt).

## Run

From the `kotlin` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=03-data-from-url --args="<url>"
```

You can give the URL in `INVOICE_DATA_URL` instead of as an argument. If the URL needs a token, put
it in `INVOICE_DATA_TOKEN` and it is sent as a bearer token.

The example writes the document to `03-data-from-url/output/`.
