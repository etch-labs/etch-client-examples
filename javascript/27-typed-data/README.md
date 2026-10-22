# 27 Typed data

This example builds an invoice from JavaScript classes instead of a plain object, and renders
`templates/typed-invoice.md`, a Markdown template. The client turns the objects into JSON itself,
like this:

| In the program | In the JSON |
|---|---|
| `issued`, a `Date` at midnight UTC | `"2026-10-08T00:00:00.000Z"` |
| `created`, a `Date` | `"2026-10-08T09:30:00.000Z"` |
| `price`, a `Decimal` (defined in the example) | a number with the exact text it was given: `19.99`, `0.10`, `1E+3` |
| `rate`, a `Decimal` with 29 digits | a number with every digit: `1.1523456789012345678901234567` |
| `reference`, renamed in `toJSON()` | `"ref"`, the name the template uses |
| `note`, `null` | `null` |

JavaScript has no decimal type, and a number is a double, which would lose digits. The example's
`Decimal` class keeps an amount as its text and writes it into the JSON as a number with
`JSON.rawJSON`, which needs Node.js 22 or later. The text goes into the JSON as it is, so write it the
way it should arrive.

Each line in the array follows the same rules. A class without `toJSON()` sends its own fields, with
their names as written. A JavaScript `Date` has no date-only form, so the client sends `issued` as a
timestamp; the template formats it as a date.

The example in every other language builds the same invoice from its own types and produces the same
document, except for the price written as `1E+3`, which shows as `1000` there. The data carries
every digit of the numbers; the document shows them as the template prints them.

The code is in [index.mjs](index.mjs).

## Run

From the `javascript` folder, after following [INSTALL.md](../INSTALL.md):

```bash
node 27-typed-data/index.mjs
```

The example prints the invoice and writes it to `27-typed-data/output/invoice-INV-2026-0042.md`.
