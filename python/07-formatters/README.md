# 07 Formatters

This example renders `templates/account-statement.docx`, a monthly bank statement, with the data in
`data/statement.json`. The data holds raw values: ISO dates, plain numbers and descriptions in
capitals as a bank feed sends them. The template formats every one of them:

| In the template | Data | Result |
|---|---|---|
| `{{period.from \| dateFormat:"D MMMM YYYY"}}` | `2026-09-01` | 1 September 2026 |
| `{{date \| dateFormat:"DD/MM/YYYY"}}` | `2026-09-02` | 02/09/2026 |
| `{{generatedAt \| timeFormat:"HH:mm"}}` | `2026-10-01T07:30:00Z` | 07:30 |
| `{{balance \| currency:"£"}}` | `3237.6` | £3,237.60 |
| `{{paidOut \| abs \| currency:"£"}}` | `-12.4` | £12.40 |
| `{{account.interestRate \| percentage}}` | `0.015` | 1.5% |
| `{{account.number \| padLeft:8:"0"}}` | `4521` | 00004521 |
| `{{customer.name \| uppercase}}` | `Example Client Ltd` | EXAMPLE CLIENT LTD |
| `{{description \| trim \| lowercase \| title \| truncate:40}}` | `BANK TRANSFER FROM SAMPLE STUDIO EXAMPLE LTD` | Bank Transfer From Sample Studio Exam... |

Formatters run left to right, so a chain like the last one trims the spaces, lowers the capitals,
capitalises each word and then shortens the result.

Paid-out amounts are negative in the data, and `abs` turns them positive before `currency` formats
them. Each transaction has an amount in either `paidIn` or `paidOut`, and the other is `null`. The
money cells are wrapped in `{{#if paidOut}}...{{/if}}`, so a `null` leaves the cell empty instead of
showing £0.00 (conditionals are the subject of the next example).

To learn more, see Formatters under Template syntax in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [main.py](main.py).

## Run

From the `python` folder, after following [INSTALL.md](../INSTALL.md):

```bash
uv run 07-formatters/main.py
```

The example writes the document to `07-formatters/output/`.
