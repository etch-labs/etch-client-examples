# 19 Error handling

This example makes three requests that fail on purpose and shows how to handle each one:

| Request | Server's code | HTTP status | Client type |
|---|---|---|---|
| Render with a wrong API key | `INVALID_API_KEY` | 401 | `AuthenticationError` |
| Render a stored template that does not exist | `TEMPLATE_NOT_FOUND` | 404 | `NotFoundError` |
| Render with data that has no `customer` | `INVALID_DATA` | 400 | `ValidationError` |

The client raises an exception for each, and every one is an `ETCHError`, so the example handles
them with `except` blocks, most specific first, then falls back to `ETCHError` for anything else.
Each handler prints `code`, `status_code` and the message (`str(error)`), and what to do next. Act
on `code`, not the message: the code stays the same when the wording changes.

The third case is the one you will meet most while building a template. The message names the
missing value (`customer.name`) and says how many others are missing too.

A request that never reaches the server, because the address is wrong or the network is down,
raises `NetworkError` or `TimeoutError`, and `status_code` is `None`.

A failed render doesn't count towards your plan's allowance.

To learn more about error handling, see Client libraries in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [main.py](main.py).

## Run

From the `python` folder, after following [INSTALL.md](../INSTALL.md):

```bash
uv run 19-error-handling/main.py
```

The example prints what happened to each request and writes no files.
