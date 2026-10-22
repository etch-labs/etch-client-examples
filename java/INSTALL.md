# Installing the Java examples

The examples use `app.get-etch:etch-client` 26.10.12, which is published on the ETCH package
registry rather than Maven Central. You need a JDK, version 17 or later, and two things from your
ETCH dashboard:

- your **package token** (Integrations page), so Gradle can download the client
- an **API key** (API keys page), which the examples use to call ETCH

The Gradle wrapper is included, so you don't need to install Gradle.

## 1. Give Gradle your package token

`build.gradle.kts` adds the registry as a repository for the `app.get-etch` group only, and reads
your package token from the `ETCH_TOKEN` environment variable. The token is never written to a
file, so the project is safe to commit. Gradle needs it only when it downloads the client.

macOS and Linux:

```bash
export ETCH_TOKEN=<your package token>
```

Windows (PowerShell):

```powershell
$env:ETCH_TOKEN = "<your package token>"
```

This setting lasts until you close the terminal. To keep the token, add the `export` line to your
shell profile (`~/.zshrc` or `~/.bashrc`), or on Windows run `setx ETCH_TOKEN <your package token>`
and open a new terminal. In CI, set `ETCH_TOKEN` as a secret environment variable.

If the first build fails with `Received status code 403 from server: Forbidden`, `ETCH_TOKEN` isn't
set in that terminal, or isn't your current token.

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

From the `java` folder.

macOS and Linux:

```bash
./gradlew run
```

Windows (PowerShell):

```powershell
.\gradlew.bat run
```

`run` starts the quick start. To run another example, give its folder:

```bash
./gradlew run -Pexample=03-data-from-url --args="<url>"
```

On Windows:

```powershell
.\gradlew.bat run -Pexample=03-data-from-url --args="<url>"
```

Each example writes its document to `output/` inside its folder.
