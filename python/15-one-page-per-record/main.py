"""Render templates/certificate.docx with the data in data/certificates.json.

{{#page-each attendees | sort "name"}} in the template repeats the page once
per attendee, in name order, with a page break between them: one Word
document holding a certificate for everyone on the course.
"""

import json
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "certificate.docx"
DATA = ROOT / "data" / "certificates.json"
OUTPUT = HERE / "output"


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))

    with ETCHClient() as client:
        result = client.process({"template": str(TEMPLATE), "data": data})

    out = OUTPUT / "certificates.docx"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
