import app.get_etch.client.BatchResult;
import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.Template;
import app.get_etch.client.models.BatchRecord;

import java.io.File;
import java.nio.file.Path;

/**
 * Renders one invoice per record in data/invoices.json, in a single request.
 *
 * <p>The template's {@code {{#document-each invoices filename="invoice-{{number}}.docx"}}} marker
 * makes the server render it once per invoice and return the documents together. The example saves
 * them to a folder and as one zip file, and reports any record that failed: one bad record fails on
 * its own, and the others are still returned.
 */
public class Batch {

    private static final Path TEMPLATE = Path.of("../templates/invoice-batch.docx");
    private static final Path DATA = Path.of("../data/invoices.json");
    private static final Path OUTPUT = Path.of("17-batch", "output");

    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // The client reads the data from the JSON file, so the data argument is null.
            ProcessOptions options = ProcessOptions.builder().dataFile(DATA).build();
            BatchResult result = client.renderBatch(Template.ofFile(TEMPLATE), null, options);

            System.out.println("Rendered " + result.getSucceeded() + " of " + result.getTotal()
                    + " invoices");
            for (BatchRecord record : result.getFailedRecords()) {
                System.out.println("  record " + (record.getIndex() + 1) + " failed: "
                        + record.getError());
            }

            for (File file : result.saveAll(OUTPUT.resolve("invoices"))) {
                System.out.println("Wrote " + Path.of("java").resolve(file.toPath()));
            }
            Path archive = OUTPUT.resolve("invoices.zip");
            result.saveZip(archive);
            System.out.println("Wrote " + Path.of("java").resolve(archive));
        }
    }
}
