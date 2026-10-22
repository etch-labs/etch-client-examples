# 20 Configuration

This example shows the settings most applications change when they create a client:

| Setting | In this example | What it is |
|---|---|---|
| Server address | `ETCH_BASE_URL`, read by `ETCHClientConfig.builder()` | Defaults to `https://api.get-etch.app`. Point it at your own ETCH server if you run one, or set it in code with `baseUrl` |
| API key | `ETCH_API_KEY`, read by `ETCHClientConfig.builder()` | Keep keys out of the code and out of version control. Example 19 sets a wrong one with `apiKey` |
| Timeout | `readTimeout` | How long to wait for the response, in a `Duration`, on each attempt |
| Retries | `maxRetries` | The number of retries **after** the first attempt, so `3` (the default) means at most four requests and `0` means no retries. All five ETCH clients count retries this way |

A request is tried again after a server error (5xx), a timeout, a dropped connection or a rate
limit, waiting a little longer each time.

The server sends the document only once it has finished the render, so the read timeout (30 seconds
unless you set it) is the one that limits how long a render may take. The builder also has
`timeout`, a limit on each attempt as a whole, which is off unless you set it, and `connectTimeout`
for opening the connection.

The example builds two clients. The first allows two minutes and three retries, which suits a PDF of
a long document, and renders the invoice to PDF. The second allows only 50 milliseconds with no
retries. The same render takes longer than that, so it fails with `TimeoutException`, and the
example prints how long it waited before giving up. A short timeout like this suits a request a user
is waiting on, where failing fast is better than hanging.

To learn more about client configuration, see Client libraries in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [Configuration.java](Configuration.java).

## Run

From the `java` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=20-configuration
```

The example writes the PDF to `20-configuration/output/`.
