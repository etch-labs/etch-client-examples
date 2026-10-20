"""Render templates/account-statement.docx with the data in data/statement.json.

The data holds raw values (ISO dates, plain numbers, upper-case descriptions)
and the template formats them: dateFormat and timeFormat for dates and times,
currency and percentage for numbers, padLeft for the account number, and a
chain such as trim | lowercase | title | truncate:40 to tidy a description.
"""

import json
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "account-statement.docx"
DATA = ROOT / "data" / "statement.json"
OUTPUT = HERE / "output"


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))

    with ETCHClient() as client:
        result = client.process({"template": str(TEMPLATE), "data": data})

    out = OUTPUT / "account-statement.docx"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
