# 27 Typed data

This example builds an invoice from Python's own types instead of a dict, and renders
`templates/typed-invoice.md`, a Markdown template. The invoice, its customer and its lines are
pydantic models, and the client turns them into JSON itself, like this:

| In the program | In the JSON |
|---|---|
| `issued`, a `date` | `"2026-10-08"` |
| `created`, a `datetime` in UTC | `"2026-10-08T09:30:00Z"` |
| `price`, a `Decimal` | a number with its exact value: `19.99`, `0.10`, and `1000` for `Decimal("1E+3")` |
| `rate`, a `Decimal` with 29 digits | a number with every digit: `1.1523456789012345678901234567` |
| `reference`, with `Field(alias="ref")` | `"ref"`, the name the template uses |
| `note`, `None` | `null` |

Each line in the list follows the same rules. The client sends field names as written; only the
alias renames one. A dataclass works the same way, except that it has no way to rename a field.

The example in every other language builds the same invoice from its own types. Java's document is
the same as this one. JavaScript has no decimal type, so its price written as `1E+3` shows as `1E+3`
there, not `1000`. The data carries every digit of the numbers; the document shows them as the
template prints them.

`uv run` installs pydantic from `pyproject.toml`, and `pip install -r requirements.txt` installs it
too.

The code is in [main.py](main.py).

## Run

From the `python` folder, after following [INSTALL.md](../INSTALL.md):

```bash
uv run 27-typed-data/main.py
```

The example prints the invoice and writes it to `27-typed-data/output/invoice-INV-2026-0042.md`.
