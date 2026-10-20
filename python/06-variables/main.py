"""Render templates/welcome-letter.docx with the data in data/welcome.json.

The template uses three kinds of variable: nested paths such as
{{customer.address.town}}, default values such as {{manager.phone | "..."}}
for data that is missing or null, and built-in variables such as {{etch.date}}
that ETCH fills in without any data.
"""

import json
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "welcome-letter.docx"
DATA = ROOT / "data" / "welcome.json"
OUTPUT = HERE / "output"


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))

    with ETCHClient() as client:
        result = client.process({"template": str(TEMPLATE), "data": data})

    out = OUTPUT / "welcome-letter.docx"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
