// Code from the Kotlin client page of the ETCH docs, section "Async (Kotlin coroutines)".
// Run from the kotlin folder with ./gradlew run -Pdocs=DocsAsync (it runs in the repository root).
// docs:start async
import app.get_etch.client.ETCHAsyncClient
import app.get_etch.client.Template
import java.nio.file.Path
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun main() {
    ETCHAsyncClient.create().use { client ->
        val letter = Template.ofFile(Path.of("templates/letter.md"))
        coroutineScope {
            val names = listOf("Example Ltd", "Sample Co")
            val results = names.map { name ->
                async {
                    client.process(
                        template = letter,
                        data = mapOf("name" to name),
                    )
                }
            }.awaitAll()
            names.zip(results).forEach { (name, result) ->
                result.saveToFile("$name.md")
            }
        }
    }
}
// docs:end async
