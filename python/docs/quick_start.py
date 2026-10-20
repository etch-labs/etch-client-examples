"""Quick start: render templates/invoice.md with data built in code."""

# docs:start quick-start
import os
from etch import ETCHClient

# The client reads your API key from the ETCH_API_KEY environment variable.
client = ETCHClient({
    "base_url": os.environ.get("ETCH_BASE_URL", "https://api.get-etch.app"),
})

result = client.process({
    "template": "templates/invoice.md",
    "data": {"name": "Example Ltd", "date": "2026-04-15", "total": "99.99"},
})
result.save_to_file("invoice-out.md")
print("Saved invoice-out.md")
# docs:end quick-start

# docs:start context-manager
config = {"base_url": os.environ.get("ETCH_BASE_URL", "https://api.get-etch.app")}
with ETCHClient(config) as invoice_client:
    result = invoice_client.process({
        "template": "templates/invoice.md",
        "data": {"name": "Example Ltd", "date": "2026-04-15",
                 "total": "99.99"},
    })
# docs:end context-manager
print("Rendered", len(result.data), "bytes with the context manager")
