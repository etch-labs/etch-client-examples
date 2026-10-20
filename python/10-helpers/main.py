"""Render templates/sales-report.docx with the data in data/sales.json.

Apart from the company and the period, the data is a list of orders, with no
totals. The template works out every figure with helpers: filter, map, unique,
sort, slice, sum, avg, max, length and join, used inline or chained with pipes
in a loop.
"""

import json
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "sales-report.docx"
DATA = ROOT / "data" / "sales.json"
OUTPUT = HERE / "output"


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))

    with ETCHClient() as client:
        result = client.process({"template": str(TEMPLATE), "data": data})

    out = OUTPUT / "sales-report.docx"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
