import app.get_etch.client.ETCHClient
import app.get_etch.client.ProcessOptions
import app.get_etch.client.Template
import app.get_etch.client.etchClientConfig
import app.get_etch.client.exceptions.TimeoutException
import app.get_etch.client.models.OutputFormat
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration

private val TEMPLATE = Path.of("../templates/invoice.docx")
private val DATA = Path.of("../data/invoice.json")
private val OUTPUT = Path.of("20-configuration", "output")

/**
 * Configures the client: where it connects, how long it waits and how often it retries.
 *
 * The API key and the server address come from the environment, never from the code. Two clients
 * are built:
 *
 * 1. One for normal use: up to two minutes to read a response, retried up to three times. It
 *    renders the invoice to PDF.
 * 2. One that gives up quickly: 50 milliseconds and no retries. The same render takes longer than
 *    that, so it fails with a TimeoutException.
 */
fun main() {
    val data: Map<String, Any?> = GsonBuilder()
        // Whole numbers stay whole (1, not 1.0), as in the JSON
        .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
        .create()
        .fromJson(Files.readString(DATA), object : TypeToken<Map<String, Any?>>() {}.type)

    // etchClientConfig reads ETCH_API_KEY, and ETCH_BASE_URL if you set it. The rendered document
    // arrives once the server has finished, so the read timeout is the one that limits how
    // long a render may take.
    val normal = etchClientConfig {
        readTimeout(Duration.ofMinutes(2))
        maxRetries(3) // retries after the first attempt
    }
    println("Connecting to ${normal.baseUrl}")
    val pdf = ProcessOptions(outputFormat = OutputFormat.PDF)
    ETCHClient.create(normal).use { client ->
        val result = client.process(Template.ofFile(TEMPLATE), data, pdf)
        Files.createDirectories(OUTPUT)
        val out = OUTPUT.resolve("invoice-${data["number"]}.pdf")
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }

    val quickConfig = etchClientConfig {
        readTimeout(Duration.ofMillis(50))
        maxRetries(0)
    }
    ETCHClient.create(quickConfig).use { quick ->
        val started = System.nanoTime()
        try {
            quick.process(Template.ofFile(TEMPLATE), data, pdf)
            println("The quick client finished in time")
        } catch (e: TimeoutException) {
            val waited = (System.nanoTime() - started) / 1e9
            println("The quick client gave up after ${"%.2f".format(waited)} seconds: ${e.code}")
        }
    }
}
