# 25 Template from a URL

This example renders the invoice from a template that lives at a URL rather than in a local file,
with the data from `data/invoice.json`. Any address that returns the template without signing in
works, for example the raw URL of `templates/invoice.docx` in this repository.

The example names the template with `Template.ofUrl`, and the client downloads it and sends it with
the request. The download is a plain request with no credentials: your API key goes only to ETCH,
never to the template's host, even after a redirect.

For a template behind a sign-in, download it yourself and pass the bytes with `Template.ofBytes`, or
store it on ETCH (see `18-stored-templates`).

To learn more about templates from URLs, see Client libraries in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [TemplateFromUrl.java](TemplateFromUrl.java).

## Run

From the `java` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=25-template-from-url --args="<url>"
```

You can give the URL in `INVOICE_TEMPLATE_URL` instead of as an argument. The example writes the
invoice to `25-template-from-url/output/invoice.docx`.
