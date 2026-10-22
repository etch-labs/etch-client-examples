# Code from the Kotlin client page

Each file here holds the Kotlin code shown on the Java & Kotlin page of the ETCH docs
(https://get-etch.app/docs-clients-java), one file per section. It is the Kotlin twin of the
classes in `java/docs`. The docs pull the lines between a `// docs:start <name>` and a
`// docs:end <name>` comment, so what you read on the page is code that has run. A section can be in
two parts, such as its imports and the code in `main`; the page shows them as one block.

Run a file from the `kotlin` folder. It runs in the repository root, so `templates/` and `data/` are
where the page says they are:

```bash
export ETCH_API_KEY="your-api-key"
./gradlew run -Pdocs=Main
./gradlew run -Pdocs=DocsBatch
```

`ETCH_BASE_URL` points them at another server. They write their output files to the repository root.
`DocsAsync` uses `kotlinx-coroutines-core`, which `build.gradle.kts` adds because the ETCH client
brings it only at run time.
