import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Builds the invoice data in code, then renders templates/invoice.docx.
 *
 * <p>The line items come from a list, the totals and tax are calculated here, and the dates are
 * based on today. The client takes the data as a map.
 */
public class DataBuiltInCode {

    private static final Path TEMPLATE = Path.of("../templates/invoice.docx");
    private static final Path OUTPUT = Path.of("02-data-built-in-code", "output");
    private static final int TAX_RATE = 8;

    record LineItem(String description, int quantity, BigDecimal unitPrice) {
        BigDecimal amount() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    private static final List<LineItem> LINE_ITEMS = List.of(
            new LineItem("Website copywriting, five pages", 5, new BigDecimal("180")),
            new LineItem("Product photography (half day)", 1, new BigDecimal("650")),
            new LineItem("Image retouching", 24, new BigDecimal("12")));

    static Map<String, Object> buildInvoice() {
        LocalDate issued = LocalDate.now();

        BigDecimal subtotal = LINE_ITEMS.stream()
                .map(LineItem::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(TAX_RATE))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        List<Map<String, Object>> items = LINE_ITEMS.stream()
                .map(item -> Map.<String, Object>of(
                        "description", item.description(),
                        "quantity", item.quantity(),
                        "unitPrice", item.unitPrice()))
                .toList();

        return Map.ofEntries(
                Map.entry("number", "DEMO-" + issued.format(DateTimeFormatter.BASIC_ISO_DATE)),
                Map.entry("issued", issued.toString()),
                Map.entry("due", issued.plusDays(30).toString()),
                Map.entry("seller", Map.of(
                        "name", "Mei Example Design",
                        "address", "12 Sample Lane, Exampleton",
                        "email", "mei@example.com")),
                Map.entry("customer", Map.of(
                        "name", "Kwame Placeholder",
                        "address", "123 Placeholder Road, Demotown")),
                Map.entry("items", items),
                Map.entry("subtotal", subtotal),
                Map.entry("taxRate", TAX_RATE),
                Map.entry("tax", tax),
                Map.entry("total", subtotal.add(tax)));
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> data = buildInvoice();

        try (ETCHClient client = ETCHClient.create()) {
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), data);

            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("invoice-" + data.get("number") + ".docx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
