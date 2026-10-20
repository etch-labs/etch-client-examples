"""Keep stored templates in folders, and render one by its path.

1. Remove anything left under example-folders by an earlier run.
2. Create the folder example-folders, and example-folders/invoices inside it.
3. Upload templates/invoice.docx into example-folders/invoices.
4. List the folders, and the templates in example-folders/invoices.
5. Render the template by its path, example-folders/invoices/invoice.docx.
6. Delete the template and both folders.
"""

import json
from pathlib import Path

from etch import ETCHClient, TemplateSource

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "invoice.docx"
DATA = ROOT / "data" / "invoice.json"
OUTPUT = HERE / "output"

TOP = "example-folders"
INVOICES = f"{TOP}/invoices"
NAME = "invoice.docx"


def example_folders(client: ETCHClient) -> list:
    """The folders under TOP, deepest first, so each is empty when its turn comes."""
    folders = [f for f in client.list_folders() if f.path == TOP or f.path.startswith(TOP + "/")]
    return sorted(folders, key=lambda f: f.path.count("/"), reverse=True)


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))

    with ETCHClient() as client:
        # A folder must be empty before it can be deleted.
        leftovers = example_folders(client)
        for folder in leftovers:
            templates, _ = client.list_templates({"folder": folder.path})
            for template in templates:
                client.delete_template(template.id)
            client.delete_folder(folder.id)
        if leftovers:
            print(f"Removed {TOP} left by an earlier run")

        client.create_folder({"name": TOP})
        print(f"Created folder {TOP}")
        client.create_folder({"name": "invoices", "parent": TOP})
        print(f"Created folder {INVOICES}")

        stored = client.upload_template({"file": str(TEMPLATE), "name": NAME, "folder": INVOICES})
        print(f"Uploaded {stored.name} to {INVOICES}")

        print(f"Folders under {TOP}:")
        for folder in sorted(example_folders(client), key=lambda f: f.path):
            print(f"  {folder.path}, templates: {folder.template_count}")

        templates, total = client.list_templates({"folder": INVOICES})
        print(f"Templates in {INVOICES}: {total}")
        for template in templates:
            print(f"  {template.name}")

        result = client.process({
            "template": f"{INVOICES}/{NAME}",
            "template_source": TemplateSource.SERVER,
            "data": data,
        })
        out = OUTPUT / f"invoice-{data['number']}.docx"
        result.save_to_file(out)
        print(f"Wrote {out.relative_to(ROOT)}")

        client.delete_template(stored.id)
        client.delete_folder(INVOICES)
        client.delete_folder(TOP)
        print(f"Deleted {NAME}, {INVOICES} and {TOP}")


if __name__ == "__main__":
    main()
