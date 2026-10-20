"""Configure the client: where it connects, how long it waits and how often it retries.

The API key and the server address come from the environment, never from the
code. Two clients are built:

1. One for normal use: up to two minutes for each attempt, and up to three
   retries after the first. It renders the invoice to PDF.
2. One that gives up quickly: 50 milliseconds and no retries. The same
   render takes longer than that, so it fails with a TimeoutError.
"""

import json
import os
import sys
import time
from pathlib import Path

import etch
from etch import ETCHClient, OutputFormat

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "invoice.docx"
DATA = ROOT / "data" / "invoice.json"
OUTPUT = HERE / "output"


def main() -> None:
    api_key = os.environ.get("ETCH_API_KEY")
    if not api_key:
        sys.exit("Set ETCH_API_KEY to your ETCH API key.")
    base_url = os.environ.get("ETCH_BASE_URL", "https://api.get-etch.app")
    print(f"Connecting to {base_url}")

    data = json.loads(DATA.read_text(encoding="utf-8"))
    request = {"template": str(TEMPLATE), "data": data, "output_format": OutputFormat.PDF}

    with ETCHClient({
        "base_url": base_url,
        "api_key": api_key,
        "timeout": 120,  # seconds for each attempt
        "max_retries": 3,  # retries after the first attempt
    }) as client:
        result = client.process(request)
        out = OUTPUT / f"invoice-{data['number']}.pdf"
        result.save_to_file(out)
        print(f"Wrote {out.relative_to(ROOT)}")

    with ETCHClient({
        "base_url": base_url,
        "api_key": api_key,
        "timeout": 0.05,
        "max_retries": 0,
    }) as quick:
        started = time.monotonic()
        try:
            quick.process(request)
            print("The quick client finished in time")
        except etch.TimeoutError as error:
            waited = time.monotonic() - started
            print(f"The quick client gave up after {waited:.2f} seconds: {error.code}")


if __name__ == "__main__":
    main()
