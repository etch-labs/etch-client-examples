# 09 Loops

This example renders `templates/order-summary.docx`, the summary of an order that ships in three
parcels, with the data in `data/order.json`. The program is the same as the quick start; the loops
are in the template:

| In the template | What it does |
|---|---|
| `{{#each shipments}} ... {{/each}}` | Repeats a heading, a table and a delivery line for each shipment |
| `{{#each items}} ... {{/each}}` in a table row | A nested loop: one table row per item in the current shipment |
| `{{#each tags}}{{this}}{{#unless @last}}, {{/unless}}{{/each}}` | A third level: the item's tags with commas between them, using `{{this}}` and `@last` |
| `Shipment {{@index1}}` and `{{#if @first}} (first to arrive){{/if}}` | Loop variables: the position counting from 1, and a note on the first shipment only |
| `{{$parent.code}}/{{@index1}}` | Reaches the shipment from inside its items loop, giving references such as `SH1/2` |
| `{{$root.customer.address}}` | Reaches the top of the data from inside the shipments loop |
| `{{#each summary}}{{@key}}: {{@value}}{{/each}}` | Loops over an object's entries |
| `{{#repeat rating}}★{{/repeat}}{{#repeat 5 - rating}}☆{{/repeat}}` | Repeats a fixed number of times: a rating out of five |

A loop that starts and ends in the same table row repeats that row; a loop whose tags are in
paragraphs of their own repeats everything between them, tables included.

The entries of an object come out in alphabetical order of key, whatever order the data lists them
in. When the order matters, use a list.

To learn more, see Loops under Template syntax in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [Loops.kt](Loops.kt).

## Run

From the `kotlin` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=09-loops
```

The example writes the document to `09-loops/output/`.
