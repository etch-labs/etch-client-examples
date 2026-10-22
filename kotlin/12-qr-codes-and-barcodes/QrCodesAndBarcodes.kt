import app.get_etch.client.ETCHClient
import java.nio.file.Files
import java.nio.file.Path

/**
 * Renders templates/shipping-label.docx with the data in data/shipping.json.
 *
 * The template turns values from the data into codes: a Code 128 barcode of the tracking
 * number, a QR code of the tracking page's address, and an EAN-13 product barcode on each line of
 * the packing slip.
 */
fun main() {
    ETCHClient.create().use { client ->
        val template = "../templates/shipping-label.docx"
        val data = Path.of("../data/shipping.json")
        val result = client.process(template, data)
        val out = Path.of("12-qr-codes-and-barcodes", "output", "shipping-label.docx")
        Files.createDirectories(out.parent)
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
