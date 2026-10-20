"""What the server says about itself and about your key."""

import os

from etch import ETCHClient

client = ETCHClient({
    "base_url": os.environ.get("ETCH_BASE_URL", "https://api.get-etch.app"),
    "api_key": os.environ["ETCH_API_KEY"],
})

# docs:start server
print(client.healthcheck().status)            # healthy
print(client.version().version)               # the server's version
caps = client.license_capabilities()
print(caps.has("template.fix"))               # True on Standard and above
print(client.whoami().customer_id)            # who the key belongs to
# docs:end server
