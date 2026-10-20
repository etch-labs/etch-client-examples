"""Make three requests that fail, and handle each failure.

1. A wrong API key: AuthenticationError.
2. A stored template that does not exist: NotFoundError.
3. Data missing a field the template needs: ValidationError.

Every client error is an ETCHError, so one handler can report any failure:
its code (such as TEMPLATE_NOT_FOUND), the HTTP status and the message.
"""

import json
import os
import sys
from pathlib import Path
from typing import Callable

from etch import (
    AuthenticationError,
    ETCHClient,
    ETCHError,
    NotFoundError,
    TemplateSource,
    ValidationError,
)

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "invoice.docx"
DATA = ROOT / "data" / "invoice.json"


def attempt(description: str, render: Callable[[], object]) -> None:
    print(description)
    try:
        render()
        print("  Rendered without an error")
    except AuthenticationError as error:
        report(error, "Check the API key in ETCH_API_KEY.")
    except NotFoundError as error:
        report(error, "Upload the template first, or check its name.")
    except ValidationError as error:
        report(error, "Add the missing values to the data, or change the template.")
    except ETCHError as error:
        report(error, "Something else went wrong; the code and message say what.")


def report(error: ETCHError, advice: str) -> None:
    print(f"  {type(error).__name__}: {error.code} (HTTP {error.status_code})")
    print(f"  {error}")
    print(f"  What to do: {advice}")


def main() -> None:
    api_key = os.environ.get("ETCH_API_KEY")
    if not api_key:
        sys.exit("Set ETCH_API_KEY to your ETCH API key.")
    base_url = os.environ.get("ETCH_BASE_URL", "https://api.get-etch.app")

    data = json.loads(DATA.read_text(encoding="utf-8"))
    without_customer = {key: value for key, value in data.items() if key != "customer"}

    with ETCHClient({"base_url": base_url, "api_key": "etch_not_a_real_key"}) as wrong_key:
        attempt(
            "1. Rendering with a wrong API key",
            lambda: wrong_key.process({"template": str(TEMPLATE), "data": data}),
        )

    with ETCHClient({"base_url": base_url, "api_key": api_key}) as client:
        attempt(
            "2. Rendering a stored template that does not exist",
            lambda: client.process({
                "template": "no-such-template.docx",
                "template_source": TemplateSource.SERVER,
                "data": data,
            }),
        )
        attempt(
            "3. Rendering with data that has no customer",
            lambda: client.process({"template": str(TEMPLATE), "data": without_customer}),
        )


if __name__ == "__main__":
    main()
