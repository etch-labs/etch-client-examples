"""Store a template in a folder, render it by name, list, then clean up."""

import os

from etch import ETCHClient, TemplateSource

client = ETCHClient({
    "base_url": os.environ.get("ETCH_BASE_URL", "https://api.get-etch.app"),
    "api_key": os.environ["ETCH_API_KEY"],
})

# An earlier run may have left the folder behind; it must be empty to delete it.
for old in client.list_folders():
    if old.path == "docs-example-invoices":
        leftovers, _ = client.list_templates({"folder": "docs-example-invoices"})
        for t in leftovers:
            client.delete_template(t.id)
        client.delete_folder(old.id)

# docs:start upload
folder = client.create_folder({"name": "docs-example-invoices"})

uploaded = client.upload_template({
    "file": "templates/invoice.md",
    "name": "invoice.md",
    "folder": folder.path,
    "tags": ["invoice", "monthly"],
})

result = client.process({
    "template": "docs-example-invoices/invoice.md",
    "template_source": TemplateSource.SERVER,
    "data": {"name": "Example Ltd", "date": "2026-04-15", "total": "99.99"},
})
# docs:end upload

# docs:start manage
# list_templates returns one page of templates and the total count.
templates, total = client.list_templates({"folder": "docs-example-invoices"})
for t in templates:
    print(t.name, t.id)

client.delete_template(uploaded.id)

for f in client.list_folders():
    print(f.path, f.template_count)
client.delete_folder(folder.id)
# docs:end manage
