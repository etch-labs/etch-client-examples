"""Typed errors: render a template that is not stored on the account."""

import json
import os

from etch import ETCHClient

client = ETCHClient({
    "base_url": os.environ.get("ETCH_BASE_URL", "https://api.get-etch.app"),
    "api_key": os.environ["ETCH_API_KEY"],
})
with open("data/invoice.json", encoding="utf-8") as f:
    data = json.load(f)

# docs:start errors
import time
from etch import (AuthenticationError, CapabilityNotLicensedError,
                  ETCHError, NotFoundError, RateLimitError, ValidationError)

try:
    result = client.process({"template": "invoice.docx", "data": data})
except AuthenticationError:
    ...  # bad or missing API key
except CapabilityNotLicensedError as e:
    print("Not on your plan:", e.capability)
except NotFoundError as e:
    print("Not found:", e)
except ValidationError as e:
    print(e.validation_errors)       # field-level details
except RateLimitError as e:
    time.sleep(e.retry_after)        # wait as long as the server asks
except ETCHError as e:
    print(e.code, e.details)         # anything else ETCH-related
# docs:end errors
