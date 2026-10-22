# 27 Typed data

This example builds an invoice from Java records instead of a map, and renders
`templates/typed-invoice.md`, a Markdown template. The client turns the records into JSON itself.
The values are sent like this:

| In the program | In the JSON |
|---|---|
| `issued`, a `LocalDate` | `"2026-10-08"` |
| `created`, an `Instant` | `"2026-10-08T09:30:00Z"` |
| `price`, a `BigDecimal` | a number with its exact value: `19.99`, `0.10`, and `1000` for `new BigDecimal("1E+3")` |
| `rate`, a `BigDecimal` with 29 digits | a number with every digit: `1.1523456789012345678901234567` |
| `reference`, with `@SerializedName("ref")` | `"ref"`, the name the template uses |
| `note`, `null` | `null` |

Each line in the list follows the same rules. Field names are sent as written; only
`@SerializedName` renames one. A class with fields, or a Kotlin data class, works the same way.

The example in every other language builds the same invoice from its own types. Python's document is
the same as this one. JavaScript has no decimal type, so its price written as `1E+3` shows as `1E+3`
there, not `1000`. The data carries every digit of the numbers; the document shows them as the
template prints them.

The code is in [TypedData.java](TypedData.java).

## Run

From the `java` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=27-typed-data
```

The example prints the invoice and writes it to `27-typed-data/output/invoice-INV-2026-0042.md`.
