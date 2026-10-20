"""Render templates/product-sheet.docx with product pictures sent as attachments.

data/products.json names each product's picture file. Each file is added to an
Attachments collection, which returns a key; the key goes in the data where the
template's {{#image photo}} reads it. The pictures travel as separate parts of
the request instead of base64 text inside the JSON.
"""

import json
from pathlib import Path

from etch import Attachments, ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "product-sheet.docx"
DATA = ROOT / "data" / "products.json"
IMAGES = ROOT / "images"
OUTPUT = HERE / "output"


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))

    attachments = Attachments()
    for product in data["products"]:
        product["photo"] = attachments.add_file(IMAGES / product["image"])

    with ETCHClient() as client:
        result = client.process({
            "template": str(TEMPLATE),
            "data": data,
            "attachments": attachments,
        })

    out = OUTPUT / "product-sheet.docx"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
