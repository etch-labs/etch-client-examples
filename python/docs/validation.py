"""Validate a template against sample data, then repair what can be repaired."""

import os

from etch import ETCHClient

client = ETCHClient({
    "base_url": os.environ.get("ETCH_BASE_URL", "https://api.get-etch.app"),
    "api_key": os.environ["ETCH_API_KEY"],
})

# docs:start validate
sample = {"customer": {"name": "Example Ltd"}}

report = client.validate_template("templates/invoice.docx", data=sample)

print(report.summary.text)
for issue in report.issues:
    print(issue.severity, issue.code + ":", issue.message)
# docs:end validate

# docs:start fix
fixed = client.fix_template("templates/invoice.docx")

print("Issues repaired:", fixed.fix.applied)
if fixed.document is not None:
    # for example invoice-fixed.docx
    with open(fixed.document_name, "wb") as f:
        f.write(fixed.document)
# docs:end fix
