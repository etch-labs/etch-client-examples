# 15 One page per record

This example renders `templates/certificate.docx` with the data in `data/certificates.json`: a
course completion certificate for each of the four attendees, one per page, in a single Word
document. The program is the same as the quick start; the template does the repeating:

| In the template | What it does |
|---|---|
| `{{#page-each attendees \| sort "name"}}` and `{{/page-each}}` on paragraphs of their own | Repeats everything between them once per attendee, with a page break between copies and none after the last |
| `\| sort "name"` | Orders the pages by name, whatever order the data lists the attendees in |
| `{{name}}`, `{{organisation}}` | The current attendee's fields |
| `{{$root.course.title}}` | Reaches the course details, which sit outside the attendee list |
| `{{#if score >= 90}}with distinction{{/if}}` | A line that appears only on some certificates |

ETCH inserts the page breaks, so the template needs none of its own. Keep each page's content a
little shorter than the page: if it fills the page to the bottom margin, the break spills onto a
page of its own and leaves a blank page between certificates.

`{{#page-each}}` works only in Word documents. Excel has `{{#sheet-each}}` for one sheet per record
(example 13), and PowerPoint has `{{#slide-each}}` for one slide per record (example 14).

To learn more about page loops, see Word under Template syntax in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [OnePagePerRecord.java](OnePagePerRecord.java).

## Run

From the `java` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=15-one-page-per-record
```

The example writes the document to `15-one-page-per-record/output/`.
