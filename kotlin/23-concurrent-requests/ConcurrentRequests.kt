import app.get_etch.client.ETCHClient
import app.get_etch.client.Template
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.concurrent.Executors

private val TEMPLATE = Path.of("../templates/invoice.docx")
private val DATA = Path.of("../data/invoices.json")
private val OUTPUT = Path.of("23-concurrent-requests", "output")

private const val LIMIT = 2

private class InvoiceBatch(val seller: Map<String, Any?>, val invoices: List<Map<String, Any?>>)

/**
 * Renders several invoices at the same time, at most two at once.
 *
 * Each invoice in data/invoices.json is its own request to templates/invoice.docx. A thread pool
 * of LIMIT threads runs them, so no more than LIMIT requests are in flight and a long list does
 * not run into the server's rate limit. The threads share one client.
 */
fun main() {
    val data = GsonBuilder()
        // Whole numbers stay whole (1, not 1.0), as in the JSON
        .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
        .create()
        .fromJson(Files.readString(DATA), InvoiceBatch::class.java)
    Files.createDirectories(OUTPUT)

    val pool = Executors.newFixedThreadPool(LIMIT)
    try {
        ETCHClient.create().use { client ->
            val renders = data.invoices.map { invoice ->
                Callable {
                    val request = invoice + ("seller" to data.seller)
                    val result = client.process(Template.ofFile(TEMPLATE), request)
                    val out = OUTPUT.resolve("invoice-${invoice["number"]}.docx")
                    result.saveToFile(out)
                    println("Wrote ${Path.of("kotlin").resolve(out)}")
                }
            }

            val started = System.nanoTime()
            for (render in pool.invokeAll(renders)) {
                render.get() // rethrows a failed render's exception
            }
            val elapsed = (System.nanoTime() - started) / 1e9
            println(
                "Rendered ${data.invoices.size} invoices in ${"%.1f".format(elapsed)} seconds, " +
                    "up to $LIMIT at a time"
            )
        }
    } finally {
        pool.shutdown()
    }
}
