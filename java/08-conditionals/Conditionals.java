import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renders templates/renewal-letter.docx once for each customer in data/renewals.json.
 *
 * <p>The same template produces a different letter for each customer: its conditionals choose the
 * greeting, describe the customer's plan, and add or leave out sentences about renewal, usage and
 * discounts.
 */
public class Conditionals {
    // Whole numbers stay whole (1, not 1.0), as in the JSON
    private static final Gson JSON = new GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create();

    private static final Path TEMPLATE = Path.of("../templates/renewal-letter.docx");
    private static final Path DATA = Path.of("../data/renewals.json");
    private static final Path OUTPUT = Path.of("08-conditionals", "output");

    record Renewals(Map<String, Object> seller, List<Map<String, Object>> customers) {}

    public static void main(String[] args) throws Exception {
        Renewals data = JSON.fromJson(Files.readString(DATA), Renewals.class);
        Files.createDirectories(OUTPUT);

        try (ETCHClient client = ETCHClient.create()) {
            for (Map<String, Object> customer : data.customers()) {
                Map<String, Object> letter = new HashMap<>(customer);
                letter.put("seller", data.seller());
                ProcessResult result = client.process(Template.ofFile(TEMPLATE), letter);

                Path out = OUTPUT.resolve("renewal-" + customer.get("accountNumber") + ".docx");
                result.saveToFile(out);
                System.out.println("Wrote " + Path.of("java").resolve(out));
            }
        }
    }
}
