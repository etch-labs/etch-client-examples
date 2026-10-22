// Code from the Java client page of the ETCH docs, section "Validate and fix a template".
// Run from the java folder with ./gradlew run -Pdocs=DocsValidation
// (it runs in the repository root).
import app.get_etch.client.ETCHClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
// docs:start validate
import app.get_etch.client.models.TemplateValidationResult;
import app.get_etch.client.models.ValidateOptions;
import app.get_etch.client.models.ValidationIssue;
// docs:end validate

public class DocsValidation {
    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // docs:start validate
            Map<String, Object> sample = Map.of("customer", Map.of("name", "Example Ltd"));

            TemplateValidationResult report = client.validateTemplate(
                Path.of("templates/invoice.docx"),
                ValidateOptions.builder().data(sample).build()
            );

            System.out.println(report.getSummary().getText());
            for (ValidationIssue issue : report.getIssues()) {
                System.out.println(issue.getSeverity() + " " + issue.getCode()
                    + ": " + issue.getMessage());
            }
            // docs:end validate

            // docs:start fix
            TemplateValidationResult fixed =
                client.fixTemplate(Path.of("templates/invoice.docx"));

            System.out.println("Repaired " + fixed.getFix().getApplied() + " issue(s)");
            if (fixed.getDocument() != null) {
                // for example invoice-fixed.docx
                Files.write(Path.of(fixed.getDocumentName()), fixed.getDocument());
            }
            // docs:end fix
        }
    }
}
