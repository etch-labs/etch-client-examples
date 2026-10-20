"""Build a client with every setting. It makes no request, because the proxy address is a placeholder."""

import os

from etch import ETCHClient

# docs:start config
client = ETCHClient({
    "base_url": os.environ.get("ETCH_BASE_URL", "https://api.get-etch.app"),
    "api_key": os.environ["ETCH_API_KEY"],
    "timeout": 60.0,                  # default 30.0 seconds
    "max_retries": 5,                 # default 3
    "headers": {"X-Request-Source": "nightly-run"},
    "verify_ssl": True,               # default True
    "proxy": "http://proxy.example:8080",  # default None
})
# docs:end config
print("Client configured")
