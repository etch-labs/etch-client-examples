# Installing the Python examples

The examples use `etch-client` 26.10.13, which is published on the ETCH package registry rather than
PyPI. You need Python 3.9 or later and two things from your ETCH dashboard:

- your package token, from the Integrations page, so your package manager can download `etch-client`
- an API key, from the API keys page, which the examples use to call ETCH

The instructions use [uv](https://docs.astral.sh/uv/getting-started/installation/). If you prefer
pip, see [Install with pip](#install-with-pip) below.

## 1. Give uv your package token

`pyproject.toml` already defines the registry as an index named `etch`, marked `explicit`, so uv
fetches only `etch-client` from it and everything else from PyPI. uv reads the credentials for that
index from two environment variables.

macOS and Linux:

```bash
export UV_INDEX_ETCH_USERNAME=__token__
export UV_INDEX_ETCH_PASSWORD=<your package token>
```

Windows (PowerShell):

```powershell
$env:UV_INDEX_ETCH_USERNAME = "__token__"
$env:UV_INDEX_ETCH_PASSWORD = "<your package token>"
```

These settings last until you close the terminal. To keep them, add the `export` lines to your shell
profile (`~/.zshrc` or `~/.bashrc`), or on Windows run `setx UV_INDEX_ETCH_USERNAME __token__` and
`setx UV_INDEX_ETCH_PASSWORD <your package token>`, then open a new terminal.

## 2. Set your API key

macOS and Linux:

```bash
export ETCH_API_KEY=<your API key>
```

Windows (PowerShell):

```powershell
$env:ETCH_API_KEY = "<your API key>"
```

The examples call `https://api.get-etch.app`. To use another ETCH server, also set `ETCH_BASE_URL`.

## 3. Run an example

From the `python` folder:

```bash
uv run 01-quick-start/main.py
```

The first run creates a virtual environment and installs `etch-client`. The example writes the
document to `01-quick-start/output/`.

## Install with pip

Create and activate a virtual environment.

macOS and Linux:

```bash
python3 -m venv .venv
source .venv/bin/activate
```

Windows (PowerShell):

```powershell
py -m venv .venv
.venv\Scripts\Activate.ps1
```

PowerShell runs `Activate.ps1` only after you allow scripts once with
`Set-ExecutionPolicy -Scope CurrentUser RemoteSigned`. In Command Prompt, activate with
`.venv\Scripts\activate.bat` instead.

Then put your package token in an environment variable and install everything in `requirements.txt`.

macOS and Linux:

```bash
export ETCH_TOKEN=<your package token>
pip install -r requirements.txt
```

Windows (PowerShell):

```powershell
$env:ETCH_TOKEN = "<your package token>"
pip install -r requirements.txt
```

The first line of `requirements.txt` is the ETCH registry's address with `${ETCH_TOKEN}` in it. pip
fills the variable in when it reads the file, on every platform, so the token never goes in the
file. pip searches both indexes. The `etch-client` project on PyPI is a placeholder owned by ETCH,
so pip installs the client from the ETCH registry. Set `ETCH_API_KEY` as in step 2, then run:

```bash
python 01-quick-start/main.py
```
