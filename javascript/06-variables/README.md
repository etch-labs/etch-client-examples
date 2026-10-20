# 06 Variables

This example renders `templates/welcome-letter.docx`, a welcome letter to a new customer, with the
data in `data/welcome.json`. The program is the same as the quick start; the template uses three
kinds of variable:

| In the template | What it does |
|---|---|
| `{{customer.address.town}}` | A nested path: each `.` goes one level into the data |
| `{{manager.phone \| "020 7946 0000, the main studio line"}}` | A default value, used because the data has no `phone` |
| `{{customer.contact.preferredChannel \| "email"}}` | A default value, used because the value is `null` |
| `{{etch.date}}`, `{{etch.version}}`, `{{etch.format}}` | Built-in variables that ETCH fills in without any data |

A variable that is missing from the data and has no default stops the render with an error such
as `key 'phone' not found in data for path 'manager.phone'`. Defaults keep a document working when
some of the data is optional.

To learn more, see Variables under Template syntax in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [index.mjs](index.mjs).

## Run

From the `javascript` folder, after following [INSTALL.md](../INSTALL.md):

```bash
node 06-variables/index.mjs
```

The example writes the document to `06-variables/output/`.
