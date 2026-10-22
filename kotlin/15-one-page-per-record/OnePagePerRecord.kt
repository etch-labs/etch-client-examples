import app.get_etch.client.ETCHClient
import java.nio.file.Files
import java.nio.file.Path

/**
 * Renders templates/certificate.docx with the data in data/certificates.json.
 *
 * {{#page-each attendees | sort "name"}} in the template repeats the page once per
 * attendee, in name order, with a page break between them: one Word document holding a certificate
 * for everyone on the course.
 */
fun main() {
    ETCHClient.create().use { client ->
        val template = "../templates/certificate.docx"
        val data = Path.of("../data/certificates.json")
        val result = client.process(template, data)
        val out = Path.of("15-one-page-per-record", "output", "certificates.docx")
        Files.createDirectories(out.parent)
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
