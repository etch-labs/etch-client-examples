// Code from the Kotlin client page of the ETCH docs, section "Render one document per record".
// Run from the kotlin folder with ./gradlew run -Pdocs=DocsBatch (it runs in the repository root).
// docs:start batch
import app.get_etch.client.ETCHClient
import app.get_etch.client.ProcessOptions
import app.get_etch.client.Template
import java.nio.file.Path
// docs:end batch

fun main() {
    ETCHClient.create().use { client ->
        // docs:start batch
        val batch = client.renderBatch(
            template = Template.ofFile(Path.of("templates/invoice-batch.docx")),
            data = null,
            options = ProcessOptions(dataFile = Path.of("data/invoices.json")),
        )

        batch.saveAll("invoices")
        println("${batch.succeeded} of ${batch.total} documents saved")
        // docs:end batch
    }
}
