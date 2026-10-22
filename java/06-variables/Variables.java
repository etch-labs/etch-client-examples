import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Renders templates/welcome-letter.docx with the data in data/welcome.json.
 *
 * <p>The template uses three kinds of variable: nested paths such as
 * {@code {{customer.address.town}}}, default values such as {@code {{manager.phone | "..."}}} for
 * data that is missing or null, and built-in variables such as {@code {{etch.date}}} that ETCH
 * fills in without any data.
 */
public class Variables {

    private static final Path TEMPLATE = Path.of("../templates/welcome-letter.docx");
    private static final Path DATA = Path.of("../data/welcome.json");
    private static final Path OUTPUT = Path.of("06-variables", "output");

    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // The client reads the data from the JSON file, so the data argument is null.
            ProcessOptions options = ProcessOptions.builder().dataFile(DATA).build();
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), null, options);

            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("welcome-letter.docx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
