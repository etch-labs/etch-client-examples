# 10 Helpers

This example renders `templates/sales-report.docx`, a month's sales report, with the data in
`data/sales.json`. Apart from the company and the period, the data is a list of twelve orders, with
no totals, averages or list of regions. The template works out every figure with helpers:

| In the template | Result |
|---|---|
| `{{sum (filter orders "status" "paid") "amount" \| currency:"£"}}` | £11,920.50, the total of the paid orders |
| `{{length (filter orders "status" "paid")}}` | 10 |
| `{{avg (filter orders "status" "paid") "amount" \| currency:"£"}}` | £1,192.05 |
| `{{max (map (filter orders "status" "paid") "amount") \| currency:"£"}}` | £2,450.00 |
| `{{join (sort (unique (map orders "rep"))) ", "}}` | The sales reps, each named once, in alphabetical order |
| `{{#each (sort (unique (map orders "region")))}}` | One table row per region, found from the orders themselves |
| `{{sum (filter (filter $root.orders "status" "paid") "region" this) "amount"}}` | Inside that loop: the paid sales for the current region |
| `{{#each orders \| filter "status" "paid" \| sort "amount" \| reverse \| slice 0 3}}` | The three largest paid orders, using the pipe form |

Helpers nest like function calls, innermost first, and a helper's result can go straight into a
formatter such as `currency`. In a loop, the pipe form reads left to right and does the same job as
nesting.

To learn more, see Helpers under Template syntax in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [Helpers.kt](Helpers.kt).

## Run

From the `kotlin` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=10-helpers
```

The example writes the document to `10-helpers/output/`.
