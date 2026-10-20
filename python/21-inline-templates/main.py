"""Render a template written in the program instead of read from a file.

The template is Markdown: an order confirmation of the kind an application
might send as an email. Its name, confirmation.md, tells ETCH what format it
is in, so the result is Markdown too. The program prints the result and saves
it to a file.
"""

import json
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
DATA = ROOT / "data" / "order.json"
OUTPUT = HERE / "output"

TEMPLATE = """\
# Order {{order.number}}

Hello {{customer.name}},

Thank you for your order of {{order.placed | dateFormat:"D MMMM YYYY"}}. It travels in {{summary.Parcels}} parcels:

{{#each shipments}}
## Parcel {{@index1}}, {{carrier}}

Arrives {{arrives | dateFormat:"dddd D MMMM"}}.

{{#each items}}
- {{qty}} x {{name}}
{{/each}}

{{/each}}
Delivering to {{customer.address}}.
"""


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))

    with ETCHClient() as client:
        result = client.process({
            "template": "confirmation.md",
            "template_content": TEMPLATE,
            "data": data,
        })

    print(result.data.decode("utf-8"))
    out = OUTPUT / "order-confirmation.md"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
