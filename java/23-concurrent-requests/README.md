# 23 Concurrent requests

This example renders the three invoices in `data/invoices.json` as three separate requests, sent at
the same time but never more than two at once. Here that is done with a fixed thread pool of `LIMIT`
threads (`Executors.newFixedThreadPool`) sharing one client; `invokeAll` waits for every render, and
`Future.get` rethrows a failure.

## Concurrent requests or a batch?

ETCH has two other ways to produce many outputs, and both happen on the server in a single request:

| Feature | One request gives | Example |
|---|---|---|
| `{{#document-each}}` | One document per record, returned together | `17-batch` |
| `{{#page-each}}`, `{{#slide-each}}`, `{{#sheet-each}}` | One document, with a page, slide or sheet per record | `15-one-page-per-record`, `14-powerpoint`, `13-excel` |

When one template covers every record, prefer those: one request, and the server does the work. This
example is about the program sending several ordinary requests in parallel, which is what you need
when each document is its own request: a different template per record, documents handled as each
one finishes, or renders mixed with other work. The three invoices here share one template, so a
batch would suit them; they stand in for documents that each need their own request.

Keep a limit on how many requests are in flight. Without one, a long list sends everything at once
and runs into the server's rate limit; the client retries a rate-limited request, but the limit
avoids the wait. Each document still counts as one render towards your plan's allowance.

The invoices finish in whatever order the server completes them, so the order of the `Wrote` lines
changes from run to run.

To learn more about concurrency, see Client libraries in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [ConcurrentRequests.java](ConcurrentRequests.java).

## Run

From the `java` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=23-concurrent-requests
```

The example writes the invoices to `23-concurrent-requests/output/`.
