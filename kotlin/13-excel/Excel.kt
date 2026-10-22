import app.get_etch.client.ETCHClient
import java.nio.file.Files
import java.nio.file.Path

/**
 * Renders templates/sales-workbook.xlsx with the data in data/sales-by-region.json.
 *
 * The template is an Excel workbook. A row loop fills each table, the total formulas below it
 * grow to cover the new rows, and {{#sheet-each regions}} copies the region sheet once for
 * each region, named after it.
 */
fun main() {
    ETCHClient.create().use { client ->
        val template = "../templates/sales-workbook.xlsx"
        val data = Path.of("../data/sales-by-region.json")
        val result = client.process(template, data)
        val out = Path.of("13-excel", "output", "sales-workbook.xlsx")
        Files.createDirectories(out.parent)
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
