// Code from the Kotlin client page of the ETCH docs, section "Quick start".
// Run from the kotlin folder with ./gradlew run -Pdocs=Main (it runs in the repository root).
// docs:start quick-start
import app.get_etch.client.ETCHClient
import app.get_etch.client.Template
import java.nio.file.Path

fun main() {
    // The client reads your API key from ETCH_API_KEY, and the server from
    // ETCH_BASE_URL if you set it.
    ETCHClient.create().use { client ->
        val result = client.process(
            template = Template.ofFile(Path.of("templates/invoice.md")),
            data = mapOf(
                "name" to "Example Ltd",
                "date" to "2026-04-15",
                "total" to "99.99",
            ),
        )
        result.saveToFile("invoice-out.md")
        println("Saved invoice-out.md")
    }
}
// docs:end quick-start
