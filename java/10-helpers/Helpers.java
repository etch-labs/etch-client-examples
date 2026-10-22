import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Renders templates/sales-report.docx with the data in data/sales.json.
 *
 * <p>The data is only a list of orders, with no totals. The template works out every figure with
 * helpers: filter, map, unique, sort, slice, sum, avg, max, length and join, used inline or chained
 * with pipes in a loop.
 */
public class Helpers {

    private static final Path TEMPLATE = Path.of("../templates/sales-report.docx");
    private static final Path DATA = Path.of("../data/sales.json");
    private static final Path OUTPUT = Path.of("10-helpers", "output");

    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // The client reads the data from the JSON file, so the data argument is null.
            ProcessOptions options = ProcessOptions.builder().dataFile(DATA).build();
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), null, options);

            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("sales-report.docx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
