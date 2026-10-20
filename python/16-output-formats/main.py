"""Render the invoice in different output formats, then convert a document to PDF.

1. Render templates/invoice.docx to Word: the format follows the template.
2. Render the same template straight to PDF with output_format.
3. Render a password-protected Word document.
4. Convert an existing document (the Word file from step 1) to PDF.
"""

import json
from pathlib import Path

from etch import ETCHClient, OutputFormat

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "invoice.docx"
DATA = ROOT / "data" / "invoice.json"
OUTPUT = HERE / "output"

# A placeholder: in a real application, take the password from configuration.
PASSWORD = "example-password"


def save(result, name: str) -> None:
    out = OUTPUT / name
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))
    name = f"invoice-{data['number']}"

    with ETCHClient() as client:
        word = client.process({"template": str(TEMPLATE), "data": data})
        save(word, f"{name}.docx")

        pdf = client.process({
            "template": str(TEMPLATE),
            "data": data,
            "output_format": OutputFormat.PDF,
        })
        save(pdf, f"{name}.pdf")

        protected = client.process({
            "template": str(TEMPLATE),
            "data": data,
            "password": PASSWORD,
        })
        save(protected, f"{name}-protected.docx")

        converted = client.convert({
            "input": str(OUTPUT / f"{name}.docx"),
            "output_format": OutputFormat.PDF,
        })
        save(converted, f"{name}-converted.pdf")


if __name__ == "__main__":
    main()
