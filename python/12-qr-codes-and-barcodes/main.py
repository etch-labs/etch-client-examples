"""Render templates/shipping-label.docx with the data in data/shipping.json.

The template turns values from the data into codes: a Code 128 barcode of the
tracking number, a QR code of the tracking page's address, and an EAN-13
product barcode on each line of the packing slip.
"""

import json
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "shipping-label.docx"
DATA = ROOT / "data" / "shipping.json"
OUTPUT = HERE / "output"


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))

    with ETCHClient() as client:
        result = client.process({"template": str(TEMPLATE), "data": data})

    out = OUTPUT / "shipping-label.docx"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
