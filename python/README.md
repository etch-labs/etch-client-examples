# Python examples

Examples for the ETCH Python client, `etch-client` 26.10.14. Each numbered folder holds one example
and a README that explains it. To learn more about the client, see Python under Client libraries in
the [ETCH documentation](https://get-etch.app/docs).

## Getting started

Follow [INSTALL.md](INSTALL.md) to install the client and set your API key, then from this folder
run:

```bash
uv run 01-quick-start/main.py
```

Each example writes its document to an `output` folder inside its own folder.

## Examples

| Example | What it shows |
|---|---|
| [01-quick-start](01-quick-start/) | Load data from a JSON file and render a Word template |
| [02-data-built-in-code](02-data-built-in-code/) | Build the data in code: line items from a list, totals and dates calculated at runtime |
| [03-data-from-url](03-data-from-url/) | Fetch the data from a URL, optionally with a bearer token |
| [04-images](04-images/) | Add a logo to the data and render a template with an image tag |
| [05-attachments](05-attachments/) | Send product pictures as attachments instead of base64 in the JSON |
| [06-variables](06-variables/) | Use nested paths, default values and built-in variables in a welcome letter |
| [07-formatters](07-formatters/) | Format dates, money, percentages and text in a bank statement |
| [08-conditionals](08-conditionals/) | Write three renewal letters from one template with if, else, unless, switch and comparisons |
| [09-loops](09-loops/) | Loop over lists and objects in an order summary, with nested loops, loop variables and repeat |
| [10-helpers](10-helpers/) | Work out a sales report's totals, averages and rankings in the template |
| [11-reusable-blocks](11-reusable-blocks/) | Define a quote's signature block once and use it for both parties |
| [12-qr-codes-and-barcodes](12-qr-codes-and-barcodes/) | Put a tracking barcode, a QR code and product barcodes on a shipping label |
| [13-excel](13-excel/) | Build an Excel workbook with a row loop, totals that follow it, and one sheet per region |
| [14-powerpoint](14-powerpoint/) | Build a PowerPoint deck with one slide per project, speaker notes and a summary table |
| [15-one-page-per-record](15-one-page-per-record/) | Make a certificate for each attendee, one per page, in a single Word document |
| [16-output-formats](16-output-formats/) | Render to Word and PDF, password-protect a document, and convert a document to PDF |
| [17-batch](17-batch/) | Render an invoice per record in one request, and save them as files and as a zip |
| [18-stored-templates](18-stored-templates/) | Store a template on ETCH, render it by name, then replace, download and delete it |
| [19-error-handling](19-error-handling/) | Handle a wrong API key, a missing template and missing data |
| [20-configuration](20-configuration/) | Set the server address, API key, timeout and retries, and watch a timeout fire |
| [21-inline-templates](21-inline-templates/) | Write a Markdown template in the program: an order confirmation for an email |
| [22-missing-values](22-missing-values/) | Choose what happens when the data lacks a value: fail, leave it out, or keep the tag |
| [23-concurrent-requests](23-concurrent-requests/) | Send several separate renders in parallel, with a limit, and compare that with a batch |
| [24-server-information](24-server-information/) | Check the server's health and version, and what the licence allows, before starting work |
| [25-template-from-url](25-template-from-url/) | Render a template downloaded from a URL, without sending the API key to the template's host |
| [26-folders](26-folders/) | Keep stored templates in folders and render one by its path |
| [27-typed-data](27-typed-data/) | Build the data from Python's own types: dates, exact decimals, a renamed field and a null |
| [28-rename-and-move](28-rename-and-move/) | Rename a stored template, move it to another folder, and render it at its new path |
