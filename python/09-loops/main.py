"""Render templates/order-summary.docx with the data in data/order.json.

The template does the looping: #each over the order's shipments, a nested
#each over each shipment's items (and a third over each item's tags), #each
over an object for the summary line, and #repeat for the star ratings.
"""

import json
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "order-summary.docx"
DATA = ROOT / "data" / "order.json"
OUTPUT = HERE / "output"


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))

    with ETCHClient() as client:
        result = client.process({"template": str(TEMPLATE), "data": data})

    out = OUTPUT / "order-summary.docx"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
