import app.get_etch.client.ETCHClient
import app.get_etch.client.Template
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.time.LocalDate

private val TEMPLATE = Path.of("../templates/typed-invoice.md")
private val OUTPUT = Path.of("27-typed-data", "output")

private data class TypedCustomer(val name: String, val email: String)

private data class TypedLine(val description: String, val quantity: Int, val price: BigDecimal)

private data class TypedInvoice(
    // The template calls it "ref"; the annotation renames the field.
    @field:SerializedName("ref") val reference: String,
    val issued: LocalDate,
    val created: Instant,
    val customer: TypedCustomer,
    val lines: List<TypedLine>,
    val rate: BigDecimal,
    val note: String?,
)

private fun buildInvoice() = TypedInvoice(
    reference = "INV-2026-0042",
    issued = LocalDate.of(2026, 10, 8),
    created = Instant.parse("2026-10-08T09:30:00Z"),
    customer = TypedCustomer("Example Ltd", "accounts@example-ltd.example"),
    lines = listOf(
        TypedLine("Paper clips, box of 100", 3, BigDecimal("19.99")),
        TypedLine("Postage, per letter", 12, BigDecimal("0.10")),
        TypedLine("Annual support", 1, BigDecimal("1E+3")),
    ),
    rate = BigDecimal("1.1523456789012345678901234567"),
    note = null,
)

/**
 * Builds the invoice from Kotlin data classes, then renders templates/typed-invoice.md.
 *
 * The invoice has a date, a timestamp, BigDecimal amounts, a field renamed with
 * `@SerializedName` and a field that is null. The client turns the data classes into JSON
 * itself: no map is built by hand.
 */
fun main() {
    val invoice = buildInvoice()

    ETCHClient.create().use { client ->
        val result = client.process(Template.ofFile(TEMPLATE), invoice)

        println(String(result.data, Charsets.UTF_8))
        Files.createDirectories(OUTPUT)
        val out = OUTPUT.resolve("invoice-${invoice.reference}.md")
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
