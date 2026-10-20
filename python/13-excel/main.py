"""Render templates/sales-workbook.xlsx with the data in data/sales-by-region.json.

The template is an Excel workbook. A row loop fills each table, the total
formulas below it grow to cover the new rows, and {{#sheet-each regions}}
copies the region sheet once for each region, named after it.
"""

import json
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "sales-workbook.xlsx"
DATA = ROOT / "data" / "sales-by-region.json"
OUTPUT = HERE / "output"


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))

    with ETCHClient() as client:
        result = client.process({"template": str(TEMPLATE), "data": data})

    out = OUTPUT / "sales-workbook.xlsx"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
