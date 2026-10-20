"""Render a template that lives at a URL rather than in a local file.

Pass the URL as the first argument or in INVOICE_TEMPLATE_URL, for example the
raw URL of templates/invoice.docx in this repository. The client downloads the
template and sends it with the data from data/invoice.json. The download is a
plain request: your ETCH API key goes only to ETCH, never to the template's host.
"""

import json
import os
import sys
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
DATA = ROOT / "data" / "invoice.json"
OUTPUT = HERE / "output"


def main() -> None:
    url = sys.argv[1] if len(sys.argv) > 1 else os.environ.get("INVOICE_TEMPLATE_URL")
    if not url:
        sys.exit("Pass the URL of the template as an argument or in INVOICE_TEMPLATE_URL.")

    data = json.loads(DATA.read_text(encoding="utf-8"))

    with ETCHClient() as client:
        result = client.process({"template": url, "data": data})

    out = OUTPUT / "invoice.docx"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
