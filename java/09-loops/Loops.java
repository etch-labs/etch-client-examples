import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Renders templates/order-summary.docx with the data in data/order.json.
 *
 * <p>The template does the looping: #each over the order's shipments, a nested #each over each
 * shipment's items (and a third over each item's tags), #each over an object for the summary line,
 * and #repeat for the star ratings.
 */
public class Loops {

    private static final Path TEMPLATE = Path.of("../templates/order-summary.docx");
    private static final Path DATA = Path.of("../data/order.json");
    private static final Path OUTPUT = Path.of("09-loops", "output");

    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // The client reads the data from the JSON file, so the data argument is null.
            ProcessOptions options = ProcessOptions.builder().dataFile(DATA).build();
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), null, options);

            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("order-summary.docx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
