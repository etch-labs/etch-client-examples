import app.get_etch.client.ETCHClient
import app.get_etch.client.ProcessOptions
import app.get_etch.client.Template
import java.nio.file.Files
import java.nio.file.Path

private val DATA = Path.of("../data/order.json")
private val OUTPUT = Path.of("21-inline-templates", "output")

// A raw string keeps the template as written. A backslash at the end of a line joins it to the
// next one, as in a Java text block.
private val TEMPLATE = """
    # Order {{order.number}}

    Hello {{customer.name}},

    Thank you for your order of {{order.placed | dateFormat:"D MMMM YYYY"}}. \
    It travels in {{summary.Parcels}} parcels:

    {{#each shipments}}
    ## Parcel {{@index1}}, {{carrier}}

    Arrives {{arrives | dateFormat:"dddd D MMMM"}}.

    {{#each items}}
    - {{qty}} x {{name}}
    {{/each}}

    {{/each}}
    Delivering to {{customer.address}}.
""".trimIndent().replace("\\\n", "") + "\n"

/**
 * Renders a template written in the program instead of read from a file.
 *
 * The template is Markdown: an order confirmation of the kind an application might send as an
 * email. Its name, confirmation.md, tells ETCH what format it is in, so the result is Markdown
 * too. The program prints the result and saves it to a file.
 */
fun main() {
    // The name gives the template's format; the bytes are the text above.
    val confirmation = Template.ofBytes("confirmation.md", TEMPLATE.toByteArray())

    ETCHClient.create().use { client ->
        // The client reads the data from the JSON file, so the data argument is null.
        val options = ProcessOptions.builder().dataFile(DATA).build()
        val result = client.process(confirmation, null, options)

        println(String(result.data, Charsets.UTF_8))
        Files.createDirectories(OUTPUT)
        val out = OUTPUT.resolve("order-confirmation.md")
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
