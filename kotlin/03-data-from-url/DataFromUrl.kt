import app.get_etch.client.ETCHClient
import app.get_etch.client.Template
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import kotlin.system.exitProcess

// Whole numbers stay whole (1, not 1.0), as in the JSON
private val JSON = GsonBuilder()
    .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
    .create()

private val TEMPLATE = Path.of("../templates/invoice.docx")
private val OUTPUT = Path.of("03-data-from-url", "output")

private fun fetchJson(url: String, token: String?): Map<String, Any> {
    val request = HttpRequest.newBuilder(URI.create(url))
        .timeout(Duration.ofSeconds(30))
        .header("Accept", "application/json")
    if (!token.isNullOrBlank()) {
        request.header("Authorization", "Bearer $token")
    }

    val response = HttpClient.newHttpClient()
        .send(request.build(), HttpResponse.BodyHandlers.ofString())
    if (response.statusCode() != 200) {
        throw IOException("GET $url returned ${response.statusCode()}")
    }
    return JSON.fromJson(response.body(), object : TypeToken<Map<String, Any>>() {})
}

/**
 * Fetches the invoice data from a URL, then renders templates/invoice.docx.
 *
 * Pass the URL as an argument
 * (`./gradlew run -Pexample=03-data-from-url --args="<url>"`) or in INVOICE_DATA_URL. Any URL
 * that returns the JSON works, for example the raw URL of data/invoice.json in this repository. If
 * the URL needs a token, put it in INVOICE_DATA_TOKEN and it is sent as a bearer token.
 */
fun main(args: Array<String>) {
    val url = args.firstOrNull() ?: System.getenv("INVOICE_DATA_URL")
    if (url.isNullOrBlank()) {
        System.err.println(
            "Pass the URL of the invoice JSON as an argument or in INVOICE_DATA_URL.",
        )
        exitProcess(1)
    }

    val data = fetchJson(url, System.getenv("INVOICE_DATA_TOKEN"))

    ETCHClient.create().use { client ->
        val result = client.process(Template.ofFile(TEMPLATE), data)

        Files.createDirectories(OUTPUT)
        val out = OUTPUT.resolve("invoice-${data["number"]}.docx")
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
