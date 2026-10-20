"""Add a logo to the invoice data, then render templates/invoice-with-logo.docx.

The template places the image with {{#image logo width="56"}}. An image in the
data is a data: URI, and image_from_file builds one from a file on disk
(image_from_bytes does the same for bytes you already have).
"""

import json
from pathlib import Path

from etch import ETCHClient, image_from_file

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "invoice-with-logo.docx"
DATA = ROOT / "data" / "invoice.json"
LOGO = ROOT / "images" / "logo.png"
OUTPUT = HERE / "output"


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))
    data["logo"] = image_from_file(LOGO)

    with ETCHClient() as client:
        result = client.process({"template": str(TEMPLATE), "data": data})

    out = OUTPUT / f"invoice-{data['number']}-with-logo.docx"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
