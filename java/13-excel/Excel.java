import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Renders templates/sales-workbook.xlsx with the data in data/sales-by-region.json.
 *
 * <p>The template is an Excel workbook. A row loop fills each table, the total formulas below it
 * grow to cover the new rows, and {@code {{#sheet-each regions}}} copies the region sheet once for
 * each region, named after it.
 */
public class Excel {

    private static final Path TEMPLATE = Path.of("../templates/sales-workbook.xlsx");
    private static final Path DATA = Path.of("../data/sales-by-region.json");
    private static final Path OUTPUT = Path.of("13-excel", "output");

    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // The client reads the data from the JSON file, so the data argument is null.
            ProcessOptions options = ProcessOptions.builder().dataFile(DATA).build();
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), null, options);

            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("sales-workbook.xlsx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
