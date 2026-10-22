import app.get_etch.client.ETCHClient
import app.get_etch.client.ProcessOptions
import app.get_etch.client.ProcessResult
import app.get_etch.client.Template
import app.get_etch.client.models.OutputFormat
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import java.nio.file.Files
import java.nio.file.Path

private val TEMPLATE = Path.of("../templates/invoice.docx")
private val DATA = Path.of("../data/invoice.json")
private val OUTPUT = Path.of("16-output-formats", "output")

// A placeholder: in a real application, take the password from configuration.
private const val PASSWORD = "example-password"

private fun save(result: ProcessResult, fileName: String) {
    val out = OUTPUT.resolve(fileName)
    result.saveToFile(out)
    println("Wrote ${Path.of("kotlin").resolve(out)}")
}

/**
 * Renders the invoice in different output formats, then converts a document to PDF.
 *
 * 1. Render templates/invoice.docx to Word: the format follows the template.
 * 2. Render the same template straight to PDF with an output format.
 * 3. Render a password-protected Word document.
 * 4. Convert an existing document (the Word file from step 1) to PDF.
 */
fun main() {
    val data: Map<String, Any?> = GsonBuilder()
        // Whole numbers stay whole (1, not 1.0), as in the JSON
        .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
        .create()
        .fromJson(Files.readString(DATA), object : TypeToken<Map<String, Any?>>() {})
    val name = "invoice-${data["number"]}"
    Files.createDirectories(OUTPUT)

    ETCHClient.create().use { client ->
        save(client.process(Template.ofFile(TEMPLATE), data), "$name.docx")

        val pdf = ProcessOptions.builder().outputFormat(OutputFormat.PDF).build()
        save(client.process(Template.ofFile(TEMPLATE), data, pdf), "$name.pdf")

        val withPassword = ProcessOptions.builder().password(PASSWORD).build()
        save(client.process(Template.ofFile(TEMPLATE), data, withPassword), "$name-protected.docx")

        val word = OUTPUT.resolve("$name.docx").toFile()
        save(client.convert(word, OutputFormat.PDF), "$name-converted.pdf")
    }
}
