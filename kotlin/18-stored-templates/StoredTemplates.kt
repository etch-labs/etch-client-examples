import app.get_etch.client.ETCHClient
import app.get_etch.client.ProcessResult
import app.get_etch.client.Template
import app.get_etch.client.imageFromFile
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import java.nio.file.Files
import java.nio.file.Path

private val TEMPLATE = Path.of("../templates/invoice.docx")
private val NEW_VERSION = Path.of("../templates/invoice-with-logo.docx")
private val DATA = Path.of("../data/invoice.json")
private val LOGO = Path.of("../images/logo.png")
private val OUTPUT = Path.of("18-stored-templates", "output")

private const val STORED_NAME = "example-invoice.docx"

private fun save(result: ProcessResult, fileName: String) {
    val out = OUTPUT.resolve(fileName)
    result.saveToFile(out)
    println("Wrote ${Path.of("kotlin").resolve(out)}")
}

private fun render(client: ETCHClient, data: Map<String, Any?>, fileName: String) =
    save(client.process(Template.stored(STORED_NAME), data), fileName)

/**
 * Stores a template on ETCH, renders it by name, replaces it, downloads it and deletes it.
 *
 * 1. Remove any copy of the template left by an earlier run.
 * 2. Upload templates/invoice.docx as example-invoice.docx.
 * 3. List the stored templates that match the name.
 * 4. Render the stored template by name.
 * 5. Replace it with templates/invoice-with-logo.docx.
 * 6. Render it by the same name again: the output now has the logo.
 * 7. Download the stored template.
 * 8. Delete it.
 */
fun main() {
    val data: MutableMap<String, Any?> = GsonBuilder()
        // Whole numbers stay whole (1, not 1.0), as in the JSON
        .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
        .create()
        .fromJson(Files.readString(DATA), object : TypeToken<MutableMap<String, Any?>>() {})
    // Only the second version of the template shows the logo; the first ignores it.
    data["logo"] = imageFromFile(LOGO)
    val number = data["number"]
    Files.createDirectories(OUTPUT)

    ETCHClient.create().use { client ->
        for (t in client.listTemplates(null, null, STORED_NAME).templates) {
            if (t.name == STORED_NAME) {
                client.deleteTemplate(t.id)
                println("Removed $STORED_NAME left by an earlier run")
            }
        }

        val stored = client.uploadTemplate(TEMPLATE.toFile(), STORED_NAME)
        println("Uploaded ${stored.name}")

        // The first two arguments filter by folder and format; null means any.
        val found = client.listTemplates(null, null, STORED_NAME)
        println("Stored templates matching $STORED_NAME: ${found.total}")
        for (t in found.templates) {
            println("  ${t.name}, ${t.size} bytes")
        }

        render(client, data, "invoice-$number.docx")

        client.updateTemplate(stored.id, NEW_VERSION.toFile())
        println("Replaced $STORED_NAME with ${NEW_VERSION.fileName}")

        render(client, data, "invoice-$number-with-logo.docx")

        save(client.downloadTemplate(stored.id), STORED_NAME)

        client.deleteTemplate(stored.id)
        println("Deleted $STORED_NAME")
    }
}
