# 01 Quick start

This example loads `data/invoice.json` and renders `templates/invoice.docx` with it. It is the
shortest complete program: create a client, render the template with the data, save the document.

The client reads your API key from `ETCH_API_KEY`, and the server address from `ETCH_BASE_URL` when
you set it (the hosted API otherwise), so neither is in the code. The program sends the JSON file
as it is, and reads only the invoice number from it, for the file name.

The same program makes an Excel workbook: change `invoice.docx` to `invoice.xlsx` in both places,
and the output is a workbook with a row per item and a subtotal that grows to fit. The template
decides the format.

On Java 25 the program can be shorter: a `void main()` with no class around it, `IO.println`, and
`Files` and `Path` without imports.

The code is in [QuickStart.java](QuickStart.java).

## Run

From the `java` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=01-quick-start
```

The example writes the document to `01-quick-start/output/`.
