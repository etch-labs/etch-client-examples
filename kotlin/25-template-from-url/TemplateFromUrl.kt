import app.get_etch.client.ETCHClient
import app.get_etch.client.ProcessOptions
import app.get_etch.client.Template
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.exitProcess

private val DATA = Path.of("../data/invoice.json")
private val OUTPUT = Path.of("25-template-from-url", "output")

/**
 * Renders a template that lives at a URL rather than in a local file.
 *
 * Pass the URL as an argument
 * (`./gradlew run -Pexample=25-template-from-url --args="<url>"`) or in INVOICE_TEMPLATE_URL, for
 * example the raw URL of templates/invoice.docx in this repository. The client downloads the
 * template and sends it with the data from data/invoice.json. The download is a plain request:
 * your ETCH API key goes only to ETCH, never to the template's host.
 */
fun main(args: Array<String>) {
    val url = args.firstOrNull() ?: System.getenv("INVOICE_TEMPLATE_URL")
    if (url.isNullOrBlank()) {
        System.err.println(
            "Pass the URL of the template as an argument or in INVOICE_TEMPLATE_URL.",
        )
        exitProcess(1)
    }

    ETCHClient.create().use { client ->
        // The client downloads the template, and reads the data from the JSON file,
        // so the data argument is null.
        val options = ProcessOptions.builder().dataFile(DATA).build()
        val result = client.process(Template.ofUrl(URI.create(url)), null, options)

        Files.createDirectories(OUTPUT)
        val out = OUTPUT.resolve("invoice.docx")
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
