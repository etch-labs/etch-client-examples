// Code from the Kotlin client page of the ETCH docs, section "Validate and fix a template".
// Run from the kotlin folder with ./gradlew run -Pdocs=DocsValidation
// (it runs in the repository root).
// docs:start validate
import app.get_etch.client.ETCHClient
import app.get_etch.client.models.ValidateOptions
import java.nio.file.Path
// docs:end validate
// docs:start fix
import java.nio.file.Files
// docs:end fix

fun main() {
    ETCHClient.create().use { client ->
        // docs:start validate
        val sample = mapOf("customer" to mapOf("name" to "Example Ltd"))

        val report = client.validateTemplate(
            Path.of("templates/invoice.docx"),
            ValidateOptions(data = sample),
        )

        println(report.summary.text)
        for (issue in report.issues) {
            println("${issue.severity} ${issue.code}: ${issue.message}")
        }
        // docs:end validate

        // docs:start fix
        val fixed = client.fixTemplate(Path.of("templates/invoice.docx"))

        println("Repaired ${fixed.fix?.applied} issue(s)")
        val document = fixed.document
        if (document != null) {
            // for example invoice-fixed.docx
            Files.write(Path.of(fixed.documentName), document)
        }
        // docs:end fix
    }
}
