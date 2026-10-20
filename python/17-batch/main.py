"""Render one invoice per record in data/invoices.json, in a single request.

The template's {{#document-each invoices filename="invoice-{{number}}.docx"}}
marker makes the server render it once per invoice and return the documents
together. The example saves them to a folder and as one zip file, and reports
any record that failed: one bad record fails on its own, and the others are
still returned.
"""

import json
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "invoice-batch.docx"
DATA = ROOT / "data" / "invoices.json"
OUTPUT = HERE / "output"


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))

    with ETCHClient() as client:
        result = client.render_batch({"template": str(TEMPLATE), "data": data})

    print(f"Rendered {result.succeeded} of {result.total} invoices")
    for record in result.failures:
        print(f"  record {record.index + 1} failed: {record.error}")

    for path in result.save_all(OUTPUT / "invoices"):
        print(f"Wrote {path.relative_to(ROOT)}")
    archive = result.save_zip(OUTPUT / "invoices.zip")
    print(f"Wrote {archive.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
