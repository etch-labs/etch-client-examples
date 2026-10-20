"""Choose what happens when the data lacks a value the template uses.

The invoice is rendered for a customer whose address is not known yet, three
times, once with each setting:

- error (the default): the render fails and names the missing value
- empty: the missing value is left out
- keep: the tag stays in the document, {{customer.address}}, so the gap shows
"""

import json
from pathlib import Path

from etch import ETCHClient, UndefinedVariables, ValidationError

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "invoice.docx"
DATA = ROOT / "data" / "invoice.json"
OUTPUT = HERE / "output"


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))
    del data["customer"]["address"]  # not known yet

    with ETCHClient() as client:
        for mode in (UndefinedVariables.ERROR, UndefinedVariables.EMPTY, UndefinedVariables.KEEP):
            try:
                result = client.process({
                    "template": str(TEMPLATE),
                    "data": data,
                    "undefined_variables": mode,
                })
            except ValidationError as error:
                print(f"{mode.value}: the render failed: {error}")
                continue
            out = OUTPUT / f"invoice-{mode.value}.docx"
            result.save_to_file(out)
            print(f"{mode.value}: wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
