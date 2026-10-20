"""Build the invoice from Python's own types and render templates/typed-invoice.md.

The invoice and its lines are pydantic models, with a date, a timestamp,
Decimal amounts, a field renamed with an alias and a field that is None. The
client turns them into JSON itself: no dict is built by hand.
"""

from datetime import date, datetime, timezone
from decimal import Decimal
from pathlib import Path
from typing import List, Optional

from pydantic import BaseModel, Field

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "typed-invoice.md"
OUTPUT = HERE / "output"


class Customer(BaseModel):
    name: str
    email: str


class Line(BaseModel):
    description: str
    quantity: int
    price: Decimal


class Invoice(BaseModel):
    # The template calls it "ref"; the alias renames the field in the JSON.
    reference: str = Field(alias="ref")
    issued: date
    created: datetime
    customer: Customer
    lines: List[Line]
    rate: Decimal
    note: Optional[str] = None


def build_invoice() -> Invoice:
    return Invoice(
        ref="INV-2026-0042",
        issued=date(2026, 10, 8),
        created=datetime(2026, 10, 8, 9, 30, tzinfo=timezone.utc),
        customer=Customer(name="Example Ltd", email="accounts@example-ltd.example"),
        lines=[
            Line(description="Paper clips, box of 100", quantity=3, price=Decimal("19.99")),
            Line(description="Postage, per letter", quantity=12, price=Decimal("0.10")),
            Line(description="Annual support", quantity=1, price=Decimal("1E+3")),
        ],
        rate=Decimal("1.1523456789012345678901234567"),
    )


def main() -> None:
    invoice = build_invoice()

    with ETCHClient() as client:
        result = client.process({"template": str(TEMPLATE), "data": invoice})

    print(result.data.decode("utf-8"))
    out = OUTPUT / f"invoice-{invoice.reference}.md"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
