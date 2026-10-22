import app.get_etch.client.ETCHClient
import app.get_etch.client.ProcessOptions
import app.get_etch.client.Template
import java.nio.file.Files
import java.nio.file.Path

private val TEMPLATE = Path.of("../templates/account-statement.docx")
private val DATA = Path.of("../data/statement.json")
private val OUTPUT = Path.of("07-formatters", "output")

/**
 * Renders templates/account-statement.docx with the data in data/statement.json.
 *
 * The data holds raw values (ISO dates, plain numbers, upper-case descriptions) and the template
 * formats them: dateFormat and timeFormat for dates and times, currency and percentage for
 * numbers, padLeft for the account number, and a chain such as
 * `trim | lowercase | title | truncate:40` to tidy a description.
 */
fun main() {
    ETCHClient.create().use { client ->
        // The client reads the data from the JSON file, so the data argument is null.
        val options = ProcessOptions(dataFile = DATA)
        val result = client.process(Template.ofFile(TEMPLATE), null, options)

        Files.createDirectories(OUTPUT)
        val out = OUTPUT.resolve("account-statement.docx")
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
