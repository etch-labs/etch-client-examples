import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;
import app.get_etch.client.models.OutputFormat;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Renders the invoice in different output formats, then converts a document to PDF.
 *
 * <ol>
 *   <li>Render templates/invoice.docx to Word: the format follows the template.
 *   <li>Render the same template straight to PDF with an output format.
 *   <li>Render a password-protected Word document.
 *   <li>Convert an existing document (the Word file from step 1) to PDF.
 * </ol>
 */
public class OutputFormats {

    private static final Path TEMPLATE = Path.of("../templates/invoice.docx");
    private static final Path DATA = Path.of("../data/invoice.json");
    private static final Path OUTPUT = Path.of("16-output-formats", "output");

    // A placeholder: in a real application, take the password from configuration.
    private static final String PASSWORD = "example-password";

    private static void save(ProcessResult result, String fileName) throws IOException {
        Path out = OUTPUT.resolve(fileName);
        result.saveToFile(out);
        System.out.println("Wrote " + Path.of("java").resolve(out));
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> data = new GsonBuilder()
                // Whole numbers stay whole (1, not 1.0), as in the JSON
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .fromJson(Files.readString(DATA), new TypeToken<Map<String, Object>>() {});
        String name = "invoice-" + data.get("number");
        Files.createDirectories(OUTPUT);

        try (ETCHClient client = ETCHClient.create()) {
            save(client.process(Template.ofFile(TEMPLATE), data), name + ".docx");

            ProcessOptions pdf = ProcessOptions.builder().outputFormat(OutputFormat.PDF).build();
            save(client.process(Template.ofFile(TEMPLATE), data, pdf), name + ".pdf");

            ProcessOptions withPassword = ProcessOptions.builder().password(PASSWORD).build();
            save(client.process(Template.ofFile(TEMPLATE), data, withPassword),
                    name + "-protected.docx");

            File word = OUTPUT.resolve(name + ".docx").toFile();
            ProcessResult converted = client.convert(word, OutputFormat.PDF);
            save(converted, name + "-converted.pdf");
        }
    }
}
