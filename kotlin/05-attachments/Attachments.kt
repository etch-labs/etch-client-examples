import app.get_etch.client.Attachments
import app.get_etch.client.ETCHClient
import app.get_etch.client.ProcessOptions
import app.get_etch.client.Template
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import java.nio.file.Files
import java.nio.file.Path

private val TEMPLATE = Path.of("../templates/product-sheet.docx")
private val DATA = Path.of("../data/products.json")
private val IMAGES = Path.of("../images")
private val OUTPUT = Path.of("05-attachments", "output")

/**
 * Renders templates/product-sheet.docx with product pictures sent as attachments.
 *
 * data/products.json names each product's picture file. Each file is added to an Attachments
 * collection, which returns a key; the key goes in the data where the template's
 * `{{#image photo}}` reads it. The pictures travel as separate parts of the request instead of
 * base64 text inside the JSON.
 */
fun main() {
    val data: MutableMap<String, Any> = GsonBuilder()
        // Whole numbers stay whole (1, not 1.0), as in the JSON
        .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
        .create()
        .fromJson(Files.readString(DATA), object : TypeToken<MutableMap<String, Any>>() {})

    val attachments = Attachments()
    @Suppress("UNCHECKED_CAST")
    for (product in data["products"] as List<MutableMap<String, Any>>) {
        val picture = IMAGES.resolve(product["image"] as String)
        product["photo"] = attachments.addFile(picture)
    }

    ETCHClient.create().use { client ->
        val options = ProcessOptions(attachments = attachments)
        val result = client.process(Template.ofFile(TEMPLATE), data, options)

        Files.createDirectories(OUTPUT)
        val out = OUTPUT.resolve("product-sheet.docx")
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
