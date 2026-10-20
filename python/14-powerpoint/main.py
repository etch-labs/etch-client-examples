"""Render templates/project-update.pptx with the data in data/projects.json.

The template is a PowerPoint deck. {{#slide-each projects}} turns its middle
slide into one slide per project, each with its own milestones and speaker
notes, and a table row loop on the last slide lists every project.
"""

import json
from pathlib import Path

from etch import ETCHClient

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TEMPLATE = ROOT / "templates" / "project-update.pptx"
DATA = ROOT / "data" / "projects.json"
OUTPUT = HERE / "output"


def main() -> None:
    data = json.loads(DATA.read_text(encoding="utf-8"))

    with ETCHClient() as client:
        result = client.process({"template": str(TEMPLATE), "data": data})

    out = OUTPUT / "project-update.pptx"
    result.save_to_file(out)
    print(f"Wrote {out.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
