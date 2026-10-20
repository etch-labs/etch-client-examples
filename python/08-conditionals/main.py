"""Render templates/renewal-letter.docx once for each customer in data/renewals.json.

The same template produces a different letter for each customer: its
conditionals choose the greeting, describe the customer's plan, and add or leave
out sentences about renewal, usage and discounts.
"""

import json
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "renewal-letter.docx"
DATA = ROOT / "data" / "renewals.json"
OUTPUT = HERE / "output"


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))

    with ETCHClient() as client:
        for customer in data["customers"]:
            letter = {**customer, "seller": data["seller"]}
            result = client.process({"template": str(TEMPLATE), "data": letter})

            out = OUTPUT / f"renewal-{customer['accountNumber']}.docx"
            result.save_to_file(out)
            print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
