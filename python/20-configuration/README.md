# 20 Configuration

This example shows the settings most applications change when they create a client:

| Setting | In this example | What it is |
|---|---|---|
| Server address | `base_url` | Read from `ETCH_BASE_URL`, defaulting to `https://api.get-etch.app`. Point it at your own ETCH server if you run one |
| API key | `api_key` | Read from `ETCH_API_KEY`. Keep keys out of the code and out of version control |
| Timeout | `timeout` | How long to wait, in seconds, for each attempt |
| Retries | `max_retries` | The number of retries **after** the first attempt, so `3` (the default) means at most four requests and `0` means no retries. All five ETCH clients count retries this way |

Before `etch-client` 26.10.9, `max_retries` counted the first attempt, so `3` meant at most three
requests. To keep the same number of requests after you upgrade, pass one less.

The client retries a request after a server error (5xx), a timeout, a dropped connection or a rate
limit, waiting a little longer each time.

The example builds two clients. The first allows two minutes and three retries, which suits a
PDF of a long document, and renders the invoice to PDF. The second allows only 50 milliseconds
with no retries. The same render takes longer than that, so it fails with `TimeoutError`, and the example
prints how long it waited before giving up. A short timeout like this suits a request a user is
waiting on, where failing fast is better than hanging.

To learn more about client configuration, see Client libraries in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [main.py](main.py).

## Run

From the `python` folder, after following [INSTALL.md](../INSTALL.md):

```bash
uv run 20-configuration/main.py
```

The example writes the PDF to `20-configuration/output/`.
