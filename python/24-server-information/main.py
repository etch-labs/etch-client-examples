"""Check the server and the licence before starting work.

1. Health: is the server up? The program stops here if it is not.
2. Version: which ETCH server is answering.
3. Licence: whose it is, its status and expiry, what it allows, and a check
   for one capability (PDF output) before relying on it.
"""

import sys

from etch import ETCHClient

CAPABILITY = "format.pdf"


def main() -> None:
    with ETCHClient() as client:
        health = client.healthcheck()
        if health.status != "healthy":
            sys.exit(f"The server at {client.base_url} is not healthy: {health.status}")

        version = client.version()
        print(f"Server: {health.status}, version {version.version}")

        licence = client.license_capabilities()
        expires = licence.expires_at.date().isoformat() if licence.expires_at else "never"
        print(f"Licence: {licence.customer}, {licence.status}, expires {expires}")
        print(f"Capabilities: {', '.join(licence.license_capabilities)}")
        print(f"PDF output: {'allowed' if licence.has(CAPABILITY) else 'not in this licence'}")


if __name__ == "__main__":
    main()
