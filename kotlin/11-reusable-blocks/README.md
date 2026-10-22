# 11 Reusable blocks

This example renders `templates/quote.docx`, a quote with two pricing packages and signature blocks
for both parties, with the data in `data/quote.json`. The program is the same as the quick start;
the template defines snippets and named values so that it says each thing once, and keeps a note for
its editor out of the document:

| In the template | What it does |
|---|---|
| `{{#define signature(party)}} ... {{/define}}` | Defines a snippet with a parameter: "Signed for" the party, with signature and date lines |
| `{{> signature(seller.name)}}` and `{{> signature($client)}}` | Uses it twice, once for each party |
| `{{#define contact}} ... {{/define}}` and `{{> contact}}` | A snippet without parameters, used at the top and the bottom of the quote |
| `{{#var client customer.company}}` | Names a value once, then uses it as `{{$client}}` |
| `{{#var depositPercent depositRate * 100}}` | Works a value out once: a rate of `0.3` becomes `30` |
| `{{#exclude}}Template note: ...{{/exclude}}` | A note for whoever edits the template, which never reaches the document |

A definition produces no output where it sits; the snippet appears wherever it is used, and takes
the formatting of the paragraph that uses it (the contact line is grey in both places because both
paragraphs are grey). In Word templates, keep snippets to blocks of text: tabs, tables and
formatting inside a snippet itself are not carried over, which is why the two packages are an
ordinary loop rather than a snippet.

To learn more about snippets, variables and `#exclude`, see Special tags under Template syntax in
the [ETCH documentation](https://get-etch.app/docs).

The code is in [ReusableBlocks.kt](ReusableBlocks.kt).

## Run

From the `kotlin` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=11-reusable-blocks
```

The example writes the document to `11-reusable-blocks/output/`.
