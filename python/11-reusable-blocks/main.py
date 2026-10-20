"""Render templates/quote.docx with the data in data/quote.json.

The template defines its signature block and contact line once as snippets
and uses them more than once, names values it reuses with #var, and keeps a
note for the template's editor out of the document with #exclude.
"""

import json
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "quote.docx"
DATA = ROOT / "data" / "quote.json"
OUTPUT = HERE / "output"


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))

    with ETCHClient() as client:
        result = client.process({"template": str(TEMPLATE), "data": data})

    out = OUTPUT / "quote.docx"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
