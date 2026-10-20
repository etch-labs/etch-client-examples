"""Rename a stored template, move it to another folder, and render it at its new path.

1. Remove anything left under example-rename by an earlier run.
2. Create the folder example-rename, and example-rename/archive inside it.
3. Upload templates/invoice.docx into example-rename as invoice-draft.docx.
4. Rename it to invoice.docx.
5. Move it into example-rename/archive.
6. Render it by its new path, example-rename/archive/invoice.docx.
7. Try two changes ETCH refuses: a new extension, and a folder that does not exist.
8. Delete the template and both folders.
"""

import json
from pathlib import Path
from typing import Callable

from etch import ETCHClient, ETCHError, TemplateSource

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "invoice.docx"
DATA = ROOT / "data" / "invoice.json"
OUTPUT = HERE / "output"

TOP = "example-rename"
ARCHIVE = f"{TOP}/archive"
DRAFT = "invoice-draft.docx"
NAME = "invoice.docx"


def example_folders(client: ETCHClient) -> list:
    """The folders under TOP, deepest first, so each is empty when its turn comes."""
    folders = [f for f in client.list_folders() if f.path == TOP or f.path.startswith(TOP + "/")]
    return sorted(folders, key=lambda f: f.path.count("/"), reverse=True)


def refused(description: str, change: Callable[[], object], reason: str) -> None:
    try:
        change()
        print(f"{description}: done")
    except ETCHError as error:
        print(f"{description}: {error.code} (HTTP {error.status_code}). {reason}")


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
        client.create_folder({"name": "archive", "parent": TOP})
        print(f"Created folder {ARCHIVE}")

        stored = client.upload_template({"file": str(TEMPLATE), "name": DRAFT, "folder": TOP})
        print(f"Uploaded {stored.name} to {stored.folder}")

        # Each change returns the template as it is now. Use the id from the
        # result for the next call: some servers give a renamed template a new id.
        renamed = client.rename_template(stored.id, NAME)
        print(f"Renamed {stored.name} to {renamed.name}")

        moved = client.move_template(renamed.id, ARCHIVE)
        print(f"Moved {moved.name} to {moved.folder}")

        result = client.process({
            "template": f"{moved.folder}/{moved.name}",
            "template_source": TemplateSource.SERVER,
            "data": data,
        })
        out = OUTPUT / f"invoice-{data['number']}.docx"
        result.save_to_file(out)
        print(f"Wrote {out.relative_to(ROOT)}")

        refused(
            f"Renaming {moved.name} to invoice.pdf",
            lambda: client.rename_template(moved.id, "invoice.pdf"),
            "A template keeps its extension.",
        )
        refused(
            f"Moving {moved.name} to {TOP}/drafts",
            lambda: client.move_template(moved.id, f"{TOP}/drafts"),
            "Create the folder first.",
        )

        client.delete_template(moved.id)
        client.delete_folder(ARCHIVE)
        client.delete_folder(TOP)
        print(f"Deleted {NAME}, {ARCHIVE} and {TOP}")


if __name__ == "__main__":
    main()
