import app.get_etch.client.ETCHClient
import app.get_etch.client.ProcessOptions
import app.get_etch.client.Template
import java.nio.file.Files
import java.nio.file.Path

private val TEMPLATE = Path.of("../templates/welcome-letter.docx")
private val DATA = Path.of("../data/welcome.json")
private val OUTPUT = Path.of("06-variables", "output")

/**
 * Renders templates/welcome-letter.docx with the data in data/welcome.json.
 *
 * The template uses three kinds of variable: nested paths such as `{{customer.address.town}}`,
 * default values such as `{{manager.phone | "..."}}` for data that is missing or null, and
 * built-in variables such as `{{etch.date}}` that ETCH fills in without any data.
 */
fun main() {
    ETCHClient.create().use { client ->
        // The client reads the data from the JSON file, so the data argument is null.
        val options = ProcessOptions(dataFile = DATA)
        val result = client.process(Template.ofFile(TEMPLATE), null, options)

        Files.createDirectories(OUTPUT)
        val out = OUTPUT.resolve("welcome-letter.docx")
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
