// Code from the Kotlin client page of the ETCH docs, section "Output formats and conversion".
// Run from the kotlin folder with ./gradlew run -Pdocs=DocsOutputFormats
// (it runs in the repository root).
// docs:start formats
import app.get_etch.client.ConvertOptions
import app.get_etch.client.ETCHClient
import app.get_etch.client.ProcessOptions
import app.get_etch.client.Template
import app.get_etch.client.models.OutputFormat
import java.io.File
import java.nio.file.Path
// docs:end formats

fun main() {
    ETCHClient.create().use { client ->
        // convert() needs a document: render the Word file first.
        client.process(
            template = Template.ofFile(Path.of("templates/invoice.docx")),
            data = null,
            options = ProcessOptions(dataFile = Path.of("data/invoice.json")),
        ).saveToFile("invoice.docx")

        // docs:start formats
        // Render a Word template straight to PDF, with the data from a JSON file
        val pdf = client.process(
            template = Template.ofFile(Path.of("templates/invoice.docx")),
            data = null,
            options = ProcessOptions(
                outputFormat = OutputFormat.PDF,
                dataFile = Path.of("data/invoice.json"),
            ),
        )
        pdf.saveToFile("invoice.pdf")

        // Convert an existing document
        val converted = client.convert(
            File("invoice.docx"),
            OutputFormat.PDF,
            ConvertOptions(outputName = "invoice-converted.pdf"),
        )
        // docs:end formats
        converted.saveToFile("invoice-converted.pdf")
        println("Saved invoice.pdf and invoice-converted.pdf")
    }
}
