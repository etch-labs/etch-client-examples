"""Render templates/invoice.docx with the data in data/invoice.json."""

import json
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
OUTPUT = HERE / "output"

data = json.loads((ROOT / "data" / "invoice.json").read_text(encoding="utf-8"))

with ETCHClient() as client:  # reads ETCH_API_KEY, and ETCH_BASE_URL if you set it
    result = client.process({
        "template": str(ROOT / "templates" / "invoice.docx"),
        "data": data,
    })

out = OUTPUT / f"invoice-{data['number']}.docx"
result.save_to_file(out)
print(f"Wrote {out.relative_to(ROOT)}")
