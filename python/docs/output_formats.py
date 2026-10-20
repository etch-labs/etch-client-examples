"""Output formats: render straight to PDF, and convert an existing document."""

import os

from etch import ETCHClient

client = ETCHClient({
    "base_url": os.environ.get("ETCH_BASE_URL", "https://api.get-etch.app"),
    "api_key": os.environ["ETCH_API_KEY"],
})

# docs:start pdf
from etch import OutputFormat

result = client.process({
    "template": "templates/invoice.docx",
    "data_file": "data/invoice.json",
    "output_format": OutputFormat.PDF,
    "output_name": "invoice-2026-001.pdf",
})
result.save_to_file("invoice-2026-001.pdf")
# docs:end pdf
print("Saved invoice-2026-001.pdf")

# convert() needs a document: render the Word file first
client.process({"template": "templates/invoice.docx", "data_file": "data/invoice.json"}).save_to_file("invoice.docx")

# docs:start convert
pdf = client.convert({
    "input": "invoice.docx",          # path or bytes
    "output_format": OutputFormat.PDF,
})
pdf.save_to_file("invoice-converted.pdf")
# docs:end convert
print("Saved invoice-converted.pdf")
