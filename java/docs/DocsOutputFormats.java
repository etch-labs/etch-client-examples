// Code from the Java client page of the ETCH docs, section "Output formats and conversion".
// Run from the java folder with ./gradlew run -Pdocs=DocsOutputFormats
// (it runs in the repository root).
import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;
import java.nio.file.Path;
// docs:start formats
import app.get_etch.client.ConvertOptions;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.models.OutputFormat;
// docs:end formats
import java.io.File;

public class DocsOutputFormats {
    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // convert() needs a document: render the Word file first.
            client.process(Template.ofFile(Path.of("templates/invoice.docx")), null,
                ProcessOptions.builder().dataFile(Path.of("data/invoice.json")).build())
                .saveToFile("invoice.docx");

            // docs:start formats
            // Render a Word template straight to PDF, with the data from a JSON file
            ProcessOptions toPdf = ProcessOptions.builder()
                .outputFormat(OutputFormat.PDF)
                .dataFile(Path.of("data/invoice.json"))
                .build();
            ProcessResult pdf = client.process(
                Template.ofFile(Path.of("templates/invoice.docx")), null, toPdf);
            pdf.saveToFile("invoice.pdf");

            // Convert an existing document
            ProcessResult converted = client.convert(
                new File("invoice.docx"), OutputFormat.PDF,
                ConvertOptions.builder().outputName("invoice-converted.pdf").build());
            // docs:end formats
            converted.saveToFile("invoice-converted.pdf");
            System.out.println("Saved invoice.pdf and invoice-converted.pdf");
        }
    }
}
