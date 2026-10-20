"""Templates: a local file, inline content, and data read from a JSON file."""

import os

from etch import ETCHClient

client = ETCHClient({
    "base_url": os.environ.get("ETCH_BASE_URL", "https://api.get-etch.app"),
    "api_key": os.environ["ETCH_API_KEY"],
})

# docs:start sources
# A local file on disk, uploaded with the request
client.process({
    "template": "templates/invoice.md",
    "data": {"name": "Example Ltd", "date": "2026-04-15", "total": "99.99"},
})

# Inline template content, no file needed
client.process({
    "template": "hello.md",
    "template_content": "Hello, {{name}}! Today is {{date}}.",
    "data": {"name": "World", "date": "2026-06-04"},
})
# docs:end sources

# docs:start data-file
result = client.process({
    "template": "templates/invoice.docx",
    "data_file": "data/invoice.json",
})
result.save_to_file("invoice.docx")
# docs:end data-file
print("Saved invoice.docx")
