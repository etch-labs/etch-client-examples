import app.get_etch.client.ETCHClient
import app.get_etch.client.ProcessOptions
import app.get_etch.client.Template
import app.get_etch.client.exceptions.ValidationException
import app.get_etch.client.models.UndefinedVariables
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import java.nio.file.Files
import java.nio.file.Path

private val TEMPLATE = Path.of("../templates/invoice.docx")
private val DATA = Path.of("../data/invoice.json")
private val OUTPUT = Path.of("22-missing-values", "output")

/**
 * Chooses what happens when the data lacks a value the template uses.
 *
 * The invoice is rendered for a customer whose address is not known yet, three times, once with
 * each setting:
 *
 * - ERROR (the default): the render fails and names the missing value
 * - EMPTY: the missing value is left out
 * - KEEP: the tag stays in the document, `{{customer.address}}`, so the gap shows
 */
fun main() {
    val data: MutableMap<String, Any?> = GsonBuilder()
        // Whole numbers stay whole (1, not 1.0), as in the JSON
        .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
        .create()
        .fromJson(Files.readString(DATA), object : TypeToken<MutableMap<String, Any?>>() {}.type)
    @Suppress("UNCHECKED_CAST")
    (data["customer"] as MutableMap<String, Any?>).remove("address") // not known yet
    Files.createDirectories(OUTPUT)

    ETCHClient.create().use { client ->
        for (mode in UndefinedVariables.values()) {
            val name = mode.name.lowercase()
            val result = try {
                val options = ProcessOptions.builder().undefinedVariables(mode).build()
                client.process(Template.ofFile(TEMPLATE), data, options)
            } catch (e: ValidationException) {
                println("$name: the render failed: ${e.message}")
                continue
            }
            val out = OUTPUT.resolve("invoice-$name.docx")
            result.saveToFile(out)
            println("$name: wrote ${Path.of("kotlin").resolve(out)}")
        }
    }
}
