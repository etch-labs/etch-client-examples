import app.get_etch.client.ETCHClient
import app.get_etch.client.Template
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import java.nio.file.Files
import java.nio.file.Path

// Whole numbers stay whole (1, not 1.0), as in the JSON
private val JSON = GsonBuilder()
    .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
    .create()

private val TEMPLATE = Path.of("../templates/renewal-letter.docx")
private val DATA = Path.of("../data/renewals.json")
private val OUTPUT = Path.of("08-conditionals", "output")

private data class Renewals(
    val seller: Map<String, Any>,
    val customers: List<Map<String, Any>>,
)

/**
 * Renders templates/renewal-letter.docx once for each customer in data/renewals.json.
 *
 * The same template produces a different letter for each customer: its conditionals choose the
 * greeting, describe the customer's plan, and add or leave out sentences about renewal, usage and
 * discounts.
 */
fun main() {
    val data = JSON.fromJson(Files.readString(DATA), Renewals::class.java)
    Files.createDirectories(OUTPUT)

    ETCHClient.create().use { client ->
        for (customer in data.customers) {
            val letter = customer + ("seller" to data.seller)
            val result = client.process(Template.ofFile(TEMPLATE), letter)

            val out = OUTPUT.resolve("renewal-${customer["accountNumber"]}.docx")
            result.saveToFile(out)
            println("Wrote ${Path.of("kotlin").resolve(out)}")
        }
    }
}
