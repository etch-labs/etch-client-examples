import app.get_etch.client.ETCHClient
import app.get_etch.client.ProcessOptions
import app.get_etch.client.Template
import java.nio.file.Files
import java.nio.file.Path

private val TEMPLATE = Path.of("../templates/order-summary.docx")
private val DATA = Path.of("../data/order.json")
private val OUTPUT = Path.of("09-loops", "output")

/**
 * Renders templates/order-summary.docx with the data in data/order.json.
 *
 * The template does the looping: #each over the order's shipments, a nested #each over each
 * shipment's items (and a third over each item's tags), #each over an object for the summary
 * line, and #repeat for the star ratings.
 */
fun main() {
    ETCHClient.create().use { client ->
        // The client reads the data from the JSON file, so the data argument is null.
        val options = ProcessOptions(dataFile = DATA)
        val result = client.process(Template.ofFile(TEMPLATE), null, options)

        Files.createDirectories(OUTPUT)
        val out = OUTPUT.resolve("order-summary.docx")
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
