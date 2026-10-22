import app.get_etch.client.ETCHClient
import java.nio.file.Files
import java.nio.file.Path

/**
 * Renders templates/quote.docx with the data in data/quote.json.
 *
 * The template defines its signature block and contact line once as snippets and uses them
 * more than once, names values it reuses with #var, and keeps a note for the template's editor out
 * of the document with #exclude.
 */
fun main() {
    ETCHClient.create().use { client ->
        val template = "../templates/quote.docx"
        val data = Path.of("../data/quote.json")
        val result = client.process(template, data)
        val out = Path.of("11-reusable-blocks", "output", "quote.docx")
        Files.createDirectories(out.parent)
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
