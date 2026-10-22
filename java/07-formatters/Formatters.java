import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Renders templates/account-statement.docx with the data in data/statement.json.
 *
 * <p>The data holds raw values (ISO dates, plain numbers, upper-case descriptions) and the template
 * formats them: dateFormat and timeFormat for dates and times, currency and percentage for numbers,
 * padLeft for the account number, and a chain such as
 * {@code trim | lowercase | title | truncate:40} to tidy a description.
 */
public class Formatters {

    private static final Path TEMPLATE = Path.of("../templates/account-statement.docx");
    private static final Path DATA = Path.of("../data/statement.json");
    private static final Path OUTPUT = Path.of("07-formatters", "output");

    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // The client reads the data from the JSON file, so the data argument is null.
            ProcessOptions options = ProcessOptions.builder().dataFile(DATA).build();
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), null, options);

            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("account-statement.docx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
