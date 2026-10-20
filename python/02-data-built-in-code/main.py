"""Build the invoice data in code, then render templates/invoice.docx.

The line items come from a list, the totals and tax are calculated here, and
the dates are based on today. Any JSON-compatible dict works as data.
"""

from datetime import date, timedelta
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "invoice.docx"
OUTPUT = HERE / "output"

TAX_RATE = 8

LINE_ITEMS = [
    # (description, quantity, unit price)
    ("Website copywriting, five pages", 5, 180),
    ("Product photography (half day)", 1, 650),
    ("Image retouching", 24, 12),
]


def build_invoice() -> dict:
    issued = date.today()
    items = [
        {"description": description, "quantity": quantity, "unitPrice": unit_price}
        for description, quantity, unit_price in LINE_ITEMS
    ]
    subtotal = sum(item["quantity"] * item["unitPrice"] for item in items)
    tax = round(subtotal * TAX_RATE / 100, 2)

    return {
        "number": f"DEMO-{issued:%Y%m%d}",
        "issued": issued.isoformat(),
        "due": (issued + timedelta(days=30)).isoformat(),
        "seller": {
            "name": "Mei Example Design",
            "address": "12 Sample Lane, Exampleton",
            "email": "mei@example.com",
        },
        "customer": {
            "name": "Kwame Placeholder",
            "address": "123 Placeholder Road, Demotown",
        },
        "items": items,
        "subtotal": subtotal,
        "taxRate": TAX_RATE,
        "tax": tax,
        "total": subtotal + tax,
    }


def main() -> None:
    data = build_invoice()

    with ETCHClient() as client:
        result = client.process({"template": str(TEMPLATE), "data": data})

    out = OUTPUT / f"invoice-{data['number']}.docx"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
