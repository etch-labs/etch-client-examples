# 19 Error handling

This example makes three requests that fail on purpose and shows how to handle each one:

| Request | Server's code | HTTP status | Client type |
|---|---|---|---|
| Render with a wrong API key | `INVALID_API_KEY` | 401 | `AuthenticationException` |
| Render a stored template that does not exist | `TEMPLATE_NOT_FOUND` | 404 | `NotFoundException` |
| Render with data that has no `customer` | `INVALID_DATA` | 400 | `ValidationException` |

The client throws an exception for each, and every one is an `ETCHException`, so the example handles
them with `catch` blocks, most specific first, then falls back to `ETCHException` for anything else.
Each handler prints `getCode()`, `getHttpStatus()` and `getMessage()`, and what to do next. Act on
`getCode()`, not the message: the code stays the same when the wording changes.

The third case is the one you will meet most while building a template. The message names the
missing value (`customer.name`) and says how many others are missing too.

A request that never reaches the server, because the address is wrong or the network is down,
throws `NetworkException` or `TimeoutException`; then `getHttpStatus()` is 0.

A failed render doesn't count towards your plan's allowance.

To learn more about error handling, see Client libraries in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [ErrorHandling.java](ErrorHandling.java).

## Run

From the `java` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=19-error-handling
```

The example prints what happened to each request and writes no files.
