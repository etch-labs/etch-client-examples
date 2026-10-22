import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Renders templates/certificate.docx with the data in data/certificates.json.
 *
 * <p>{@code {{#page-each attendees | sort "name"}}} in the template repeats the page once per
 * attendee, in name order, with a page break between them: one Word document holding a certificate
 * for everyone on the course.
 */
public class OnePagePerRecord {

    private static final Path TEMPLATE = Path.of("../templates/certificate.docx");
    private static final Path DATA = Path.of("../data/certificates.json");
    private static final Path OUTPUT = Path.of("15-one-page-per-record", "output");

    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // The client reads the data from the JSON file, so the data argument is null.
            ProcessOptions options = ProcessOptions.builder().dataFile(DATA).build();
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), null, options);

            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("certificates.docx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
