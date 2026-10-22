# 08 Conditionals

This example renders `templates/renewal-letter.docx` once for each of the three customers in
`data/renewals.json`, so the same template produces three different letters. The program loops
over the customers and adds the shared `seller` details to each one before rendering.

The template's conditionals decide what each letter says:

| In the template | What it does |
|---|---|
| `{{#if contact.firstName}}Dear {{contact.firstName}},{{else}}Dear {{company}} team,{{/if}}` | Greets by first name when there is one |
| `{{#switch plan}}{{#case "free"}}...{{/case}}{{#case "standard"}}...{{/case}}...{{/switch}}` | One sentence per plan |
| `{{#if plan == "free"}}...{{else if daysUntilRenewal <= 14}}...{{else}}...{{/if}}` | Says "renews in 9 days" when renewal is close, the date otherwise |
| `{{#unless autoRenew}}...{{/unless}}` | A reminder only for customers with automatic renewal off |
| `{{#if plan == "free" \|\| usagePercent >= 80}}...{{/if}}` | Suggests an upgrade to Free customers and anyone near their limit |
| `{{#if promoCode ^= "LOYAL"}}...{{/if}}` | A discount line for codes that start with `LOYAL` |

A sentence whose condition is false is removed together with its paragraph, so the letters have no
gaps. A key that is missing from the data counts as false, as do `null`, `""`, `0` and `false`.

To learn more, see Conditionals under Template syntax in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [Conditionals.kt](Conditionals.kt).

## Run

From the `kotlin` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=08-conditionals
```

The example writes the three letters to `08-conditionals/output/`, one per account number.
