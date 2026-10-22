import app.get_etch.client.ETCHClient
import app.get_etch.client.ProcessOptions
import app.get_etch.client.Template
import java.nio.file.Files
import java.nio.file.Path

private val TEMPLATE = Path.of("../templates/sales-report.docx")
private val DATA = Path.of("../data/sales.json")
private val OUTPUT = Path.of("10-helpers", "output")

/**
 * Renders templates/sales-report.docx with the data in data/sales.json.
 *
 * The data is only a list of orders, with no totals. The template works out every figure with
 * helpers: filter, map, unique, sort, slice, sum, avg, max, length and join, used inline or
 * chained with pipes in a loop.
 */
fun main() {
    ETCHClient.create().use { client ->
        // The client reads the data from the JSON file, so the data argument is null.
        val options = ProcessOptions(dataFile = DATA)
        val result = client.process(Template.ofFile(TEMPLATE), null, options)

        Files.createDirectories(OUTPUT)
        val out = OUTPUT.resolve("sales-report.docx")
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
