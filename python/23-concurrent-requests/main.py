"""Render several invoices at the same time, at most two at once.

Each invoice in data/invoices.json is its own request to templates/invoice.docx,
sent through the asynchronous client. A semaphore keeps no more than LIMIT
requests in flight, so a long list does not run into the server's rate limit.
"""

import asyncio
import json
import time
from pathlib import Path

from etch import AsyncETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "invoice.docx"
DATA = ROOT / "data" / "invoices.json"
OUTPUT = HERE / "output"

LIMIT = 2


async def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))
    slots = asyncio.Semaphore(LIMIT)

    async with AsyncETCHClient() as client:

        async def render(invoice: dict) -> None:
            async with slots:
                result = await client.process({
                    "template": str(TEMPLATE),
                    "data": {**invoice, "seller": data["seller"]},
                })
            out = OUTPUT / f"invoice-{invoice['number']}.docx"
            result.save_to_file(out)
            print(f"Wrote {out.relative_to(ROOT)}")

        started = time.monotonic()
        await asyncio.gather(*(render(invoice) for invoice in data["invoices"]))

    elapsed = time.monotonic() - started
    print(f"Rendered {len(data['invoices'])} invoices in {elapsed:.1f} seconds, up to {LIMIT} at a time")


if __name__ == "__main__":
    asyncio.run(main())
