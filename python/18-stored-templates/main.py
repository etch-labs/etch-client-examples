"""Store a template on ETCH, render it by name, replace it, download it and delete it.

1. Remove any copy of the template left by an earlier run.
2. Upload templates/invoice.docx as example-invoice.docx.
3. List the stored templates that match the name.
4. Render the stored template by name.
5. Replace it with templates/invoice-with-logo.docx.
6. Render it by the same name again: the output now has the logo.
7. Download the stored template.
8. Delete it.
"""

import json
from pathlib import Path

from etch import ETCHClient, TemplateSource, image_from_file

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "invoice.docx"
NEW_VERSION = ROOT / "templates" / "invoice-with-logo.docx"
DATA = ROOT / "data" / "invoice.json"
LOGO = ROOT / "images" / "logo.png"
OUTPUT = HERE / "output"

STORED_NAME = "example-invoice.docx"


def render(client: ETCHClient, data: dict, file_name: str) -> None:
    result = client.process({
        "template": STORED_NAME,
        "template_source": TemplateSource.SERVER,
        "data": data,
    })
    out = OUTPUT / file_name
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))
    # Only the second version of the template shows the logo; the first ignores it.
    data["logo"] = image_from_file(LOGO)
    number = data["number"]

    with ETCHClient() as client:
        templates, _ = client.list_templates({"search": STORED_NAME})
        for template in templates:
            if template.name == STORED_NAME:
                client.delete_template(template.id)
                print(f"Removed {STORED_NAME} left by an earlier run")

        stored = client.upload_template({"file": str(TEMPLATE), "name": STORED_NAME})
        print(f"Uploaded {stored.name}")

        templates, total = client.list_templates({"search": STORED_NAME})
        print(f"Stored templates matching {STORED_NAME}: {total}")
        for template in templates:
            print(f"  {template.name}, {template.size} bytes")

        render(client, data, f"invoice-{number}.docx")

        client.update_template(stored.id, str(NEW_VERSION))
        print(f"Replaced {STORED_NAME} with {NEW_VERSION.name}")

        render(client, data, f"invoice-{number}-with-logo.docx")

        downloaded = client.download_template(stored.id)
        out = OUTPUT / STORED_NAME
        downloaded.save_to_file(out)
        print(f"Wrote {out.relative_to(ROOT)}")

        client.delete_template(stored.id)
        print(f"Deleted {STORED_NAME}")


if __name__ == "__main__":
    main()
