# 13 Excel

This example renders `templates/sales-workbook.xlsx`, an Excel workbook, with the data in
`data/sales-by-region.json`. The program is the same as the quick start apart from the template: the
output is an `.xlsx` because the template is one.

The workbook has a **Summary** sheet with a row per region, then one sheet per region listing its
orders. The template has only two sheets:

| In the template | What it does |
|---|---|
| `{{#each regions}}{{name}}` in A5 and `...{{/each}}` in C5 of Summary | A row loop: row 5 is repeated once per region |
| `{{length orders}}` and `{{sum orders "amount"}}` | Helpers work in cells too, and their results stay numbers |
| `=SUM(C5:C5)` in the total row | Formulas below a loop follow it: this becomes `=SUM(C5:C8)` for four regions |
| `{{#sheet-each regions}}` in A1 of the second sheet | Copies that sheet once per region and removes the original |
| A sheet named `{{name}}` | Each copy is named after its region: East, North, South, West |
| `{{$root.company}}` on the region sheet | Inside a sheet loop, `$root` reaches the rest of the data |

Values that are numbers in the data arrive as numbers in their cells, so the formulas and the
`£#,##0.00` number format set in the template work on them. The template's fonts, fills, borders and
column widths carry over to every row and every copied sheet.

`#sheet-each` doesn't group records for you, so `data/sales-by-region.json` already lists the orders
by region.

To learn more about Excel templates, see Excel under Template syntax in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [Excel.kt](Excel.kt).

## Run

From the `kotlin` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=13-excel
```

The example writes the workbook to `13-excel/output/`.
