"""Render several documents at once with the async client."""

# docs:start async
import asyncio
import os
from etch import AsyncETCHClient

async def main():
    async with AsyncETCHClient({
        "base_url": os.environ.get("ETCH_BASE_URL", "https://api.get-etch.app"),
        "api_key": os.environ["ETCH_API_KEY"],
    }) as client:
        results = await asyncio.gather(*[
            client.process({
                "template": "templates/letter.md",
                "data": {"name": name},
            })
            for name in ["Example Ltd", "Sample Co"]
        ])
        for r in results:
            print(len(r.data), "bytes")

asyncio.run(main())
# docs:end async
