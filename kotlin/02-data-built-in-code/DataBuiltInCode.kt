import app.get_etch.client.ETCHClient
import app.get_etch.client.Template
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val TEMPLATE = Path.of("../templates/invoice.docx")
private val OUTPUT = Path.of("02-data-built-in-code", "output")
private const val TAX_RATE = 8

private data class LineItem(val description: String, val quantity: Int, val unitPrice: BigDecimal) {
    val amount: BigDecimal get() = unitPrice * quantity.toBigDecimal()
}

private val LINE_ITEMS = listOf(
    LineItem("Website copywriting, five pages", 5, BigDecimal("180")),
    LineItem("Product photography (half day)", 1, BigDecimal("650")),
    LineItem("Image retouching", 24, BigDecimal("12")),
)

private fun buildInvoice(): Map<String, Any> {
    val issued = LocalDate.now()

    val subtotal = LINE_ITEMS.sumOf { it.amount }
    val tax = (subtotal * TAX_RATE.toBigDecimal())
        .divide(BigDecimal(100), 2, RoundingMode.HALF_UP)

    return mapOf(
        "number" to "DEMO-" + issued.format(DateTimeFormatter.BASIC_ISO_DATE),
        "issued" to issued.toString(),
        "due" to issued.plusDays(30).toString(),
        "seller" to mapOf(
            "name" to "Mei Example Design",
            "address" to "12 Sample Lane, Exampleton",
            "email" to "mei@example.com",
        ),
        "customer" to mapOf(
            "name" to "Kwame Placeholder",
            "address" to "123 Placeholder Road, Demotown",
        ),
        "items" to LINE_ITEMS.map {
            mapOf(
                "description" to it.description,
                "quantity" to it.quantity,
                "unitPrice" to it.unitPrice,
            )
        },
        "subtotal" to subtotal,
        "taxRate" to TAX_RATE,
        "tax" to tax,
        "total" to subtotal + tax,
    )
}

/**
 * Builds the invoice data in code, then renders templates/invoice.docx.
 *
 * The line items come from a list, the totals and tax are calculated here, and the dates are
 * based on today. The client takes the data as a map.
 */
fun main() {
    val data = buildInvoice()

    ETCHClient.create().use { client ->
        val result = client.process(Template.ofFile(TEMPLATE), data)

        Files.createDirectories(OUTPUT)
        val out = OUTPUT.resolve("invoice-${data["number"]}.docx")
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
