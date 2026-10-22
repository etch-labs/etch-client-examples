import app.get_etch.client.ETCHClient
import app.get_etch.client.ProcessOptions
import app.get_etch.client.Template
import java.nio.file.Path

private val TEMPLATE = Path.of("../templates/invoice-batch.docx")
private val DATA = Path.of("../data/invoices.json")
private val OUTPUT = Path.of("17-batch", "output")

/**
 * Renders one invoice per record in data/invoices.json, in a single request.
 *
 * The template's {{#document-each invoices filename="invoice-{{number}}.docx"}} marker
 * makes the server render it once per invoice and return the documents together. The example saves
 * them to a folder and as one zip file, and reports any record that failed: one bad record fails on
 * its own, and the others are still returned.
 */
fun main() {
    ETCHClient.create().use { client ->
        // The client reads the data from the JSON file, so the data argument is null.
        val options = ProcessOptions.builder().dataFile(DATA).build()
        val result = client.renderBatch(Template.ofFile(TEMPLATE), null, options)

        println("Rendered ${result.succeeded} of ${result.total} invoices")
        for (record in result.failedRecords) {
            println("  record ${record.index + 1} failed: ${record.error}")
        }

        for (file in result.saveAll(OUTPUT.resolve("invoices"))) {
            println("Wrote ${Path.of("kotlin").resolve(file.toPath())}")
        }
        val archive = OUTPUT.resolve("invoices.zip")
        result.saveZip(archive)
        println("Wrote ${Path.of("kotlin").resolve(archive)}")
    }
}
