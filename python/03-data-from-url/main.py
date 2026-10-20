"""Fetch the invoice data from a URL, then render templates/invoice.docx.

Pass the URL as the first argument or in INVOICE_DATA_URL. Any URL that returns
the JSON works, for example the raw URL of data/invoice.json in this repository.
If the URL needs a token, put it in INVOICE_DATA_TOKEN and the example sends it
as a bearer token.
"""

import json
import os
import sys
import urllib.request
from pathlib import Path
from typing import Optional

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "invoice.docx"
OUTPUT = HERE / "output"


def fetch_json(url: str, token: Optional[str]) -> dict:
    request = urllib.request.Request(url, headers={"Accept": "application/json"})
    if token:
        request.add_header("Authorization", f"Bearer {token}")
    with urllib.request.urlopen(request, timeout=30) as response:
        return json.load(response)


def main() -> None:
    url = sys.argv[1] if len(sys.argv) > 1 else os.environ.get("INVOICE_DATA_URL")
    if not url:
        sys.exit("Pass the URL of the invoice JSON as an argument or in INVOICE_DATA_URL.")

    data = fetch_json(url, os.environ.get("INVOICE_DATA_TOKEN"))

    with ETCHClient() as client:
        result = client.process({"template": str(TEMPLATE), "data": data})

    out = OUTPUT / f"invoice-{data['number']}.docx"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
