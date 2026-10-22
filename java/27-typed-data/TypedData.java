import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Builds the invoice from Java records, then renders templates/typed-invoice.md.
 *
 * <p>The invoice has a date, a timestamp, BigDecimal amounts, a field renamed with
 * {@code @SerializedName} and a field that is null. The client turns the records into JSON
 * itself: no map is built by hand.
 */
public class TypedData {

    private static final Path TEMPLATE = Path.of("../templates/typed-invoice.md");
    private static final Path OUTPUT = Path.of("27-typed-data", "output");

    record Customer(String name, String email) {}

    record Line(String description, int quantity, BigDecimal price) {}

    record Invoice(
            // The template calls it "ref"; the annotation renames the field.
            @SerializedName("ref") String reference,
            LocalDate issued,
            Instant created,
            Customer customer,
            List<Line> lines,
            BigDecimal rate,
            String note) {}

    static Invoice buildInvoice() {
        return new Invoice(
                "INV-2026-0042",
                LocalDate.of(2026, 10, 8),
                Instant.parse("2026-10-08T09:30:00Z"),
                new Customer("Example Ltd", "accounts@example-ltd.example"),
                List.of(
                        new Line("Paper clips, box of 100", 3, new BigDecimal("19.99")),
                        new Line("Postage, per letter", 12, new BigDecimal("0.10")),
                        new Line("Annual support", 1, new BigDecimal("1E+3"))),
                new BigDecimal("1.1523456789012345678901234567"),
                null);
    }

    public static void main(String[] args) throws Exception {
        Invoice invoice = buildInvoice();

        try (ETCHClient client = ETCHClient.create()) {
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), invoice);

            System.out.println(new String(result.getData(), StandardCharsets.UTF_8));
            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("invoice-" + invoice.reference() + ".md");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
