import app.get_etch.client.ETCHClient
import app.get_etch.client.imageFromFile
import app.get_etch.client.Template
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import java.nio.file.Files
import java.nio.file.Path

private val TEMPLATE = Path.of("../templates/invoice-with-logo.docx")
private val DATA = Path.of("../data/invoice.json")
private val LOGO = Path.of("../images/logo.png")
private val OUTPUT = Path.of("04-images", "output")

/**
 * Adds a logo to the invoice data, then renders templates/invoice-with-logo.docx.
 *
 * The template places the image with `{{#image logo width="56"}}`. An image in the data is a
 * data: URI, and `Images.imageFromFile` builds one from a file on disk (`Images.imageFromBytes`
 * does the same for bytes you already have).
 */
fun main() {
    val data: Map<String, Any> = GsonBuilder()
        // Whole numbers stay whole (1, not 1.0), as in the JSON
        .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
        .create()
        .fromJson(Files.readString(DATA), object : TypeToken<Map<String, Any>>() {})

    ETCHClient.create().use { client ->
        val withLogo = data + ("logo" to imageFromFile(LOGO))
        val result = client.process(Template.ofFile(TEMPLATE), withLogo)

        Files.createDirectories(OUTPUT)
        val out = OUTPUT.resolve("invoice-${data["number"]}-with-logo.docx")
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
