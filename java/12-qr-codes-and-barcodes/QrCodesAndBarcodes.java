import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Renders templates/shipping-label.docx with the data in data/shipping.json.
 *
 * <p>The template turns values from the data into codes: a Code 128 barcode of the tracking
 * number, a QR code of the tracking page's address, and an EAN-13 product barcode on each line of
 * the packing slip.
 */
public class QrCodesAndBarcodes {

    private static final Path TEMPLATE = Path.of("../templates/shipping-label.docx");
    private static final Path DATA = Path.of("../data/shipping.json");
    private static final Path OUTPUT = Path.of("12-qr-codes-and-barcodes", "output");

    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // The client reads the data from the JSON file, so the data argument is null.
            ProcessOptions options = ProcessOptions.builder().dataFile(DATA).build();
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), null, options);

            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("shipping-label.docx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
