// Code from the Java client page of the ETCH docs, section "Batches: one document per record".
// Run from the java folder with ./gradlew run -Pdocs=DocsBatch (it runs in the repository root).
import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.Template;
import java.nio.file.Path;
// docs:start batch
import app.get_etch.client.BatchResult;
// docs:end batch

public class DocsBatch {
    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // docs:start batch
            BatchResult batch = client.renderBatch(
                Template.ofFile(Path.of("templates/invoice-batch.docx")), null,
                ProcessOptions.builder()
                    .dataFile(Path.of("data/invoices.json"))
                    .build());

            batch.saveAll("invoices");
            System.out.println(
                batch.getSucceeded() + " of " + batch.getTotal() + " documents saved");
            // docs:end batch
        }
    }
}
