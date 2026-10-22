// Code from the Kotlin client page of the ETCH docs, section "Choose where the template comes
// from".
// Run from the kotlin folder with ./gradlew run -Pdocs=DocsTemplates
// (it runs in the repository root).
// docs:start templates
import app.get_etch.client.ETCHClient
import app.get_etch.client.Template
import java.io.File
import java.nio.file.Path
// docs:end templates

fun main() {
    ETCHClient.create().use { client ->
        // docs:start templates
        val data = mapOf("name" to "Example Ltd")

        // A file on disk, uploaded with the request
        val fromFile = client.process(
            template = Template.ofFile(Path.of("templates/letter.md")),
            data = data,
        )

        // Template text you already have in memory; the name's extension
        // gives the format
        val text = "Hello, {{name}}!".toByteArray()
        val fromBytes = client.process(
            template = Template.ofBytes("hello.md", text),
            data = data,
        )

        // A template stored on your account. Upload it once:
        val stored = client.uploadTemplate(File("templates/letter.md"), "letter.md")
        val fromStored = client.process(
            template = Template.stored("letter.md"),
            data = data,
        )
        // docs:end templates
        println("${fromFile.data.size}, ${fromBytes.data.size} and ${fromStored.data.size} bytes")
        client.deleteTemplate(stored.id)
    }
}
