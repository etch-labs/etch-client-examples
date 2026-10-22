import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;
import app.get_etch.client.exceptions.ValidationException;
import app.get_etch.client.models.UndefinedVariables;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Chooses what happens when the data lacks a value the template uses.
 *
 * <p>The invoice is rendered for a customer whose address is not known yet, three times, once with
 * each setting:
 *
 * <ul>
 *   <li>ERROR (the default): the render fails and names the missing value
 *   <li>EMPTY: the missing value is left out
 *   <li>KEEP: the tag stays in the document, {@code {{customer.address}}}, so the gap shows
 * </ul>
 */
public class MissingValues {

    private static final Path TEMPLATE = Path.of("../templates/invoice.docx");
    private static final Path DATA = Path.of("../data/invoice.json");
    private static final Path OUTPUT = Path.of("22-missing-values", "output");

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        Map<String, Object> data = new GsonBuilder()
                // Whole numbers stay whole (1, not 1.0), as in the JSON
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .fromJson(Files.readString(DATA), new TypeToken<Map<String, Object>>() {});
        ((Map<String, Object>) data.get("customer")).remove("address"); // not known yet
        Files.createDirectories(OUTPUT);

        try (ETCHClient client = ETCHClient.create()) {
            for (UndefinedVariables mode : UndefinedVariables.values()) {
                String name = mode.name().toLowerCase();
                ProcessResult result;
                try {
                    ProcessOptions options =
                            ProcessOptions.builder().undefinedVariables(mode).build();
                    result = client.process(Template.ofFile(TEMPLATE), data, options);
                } catch (ValidationException e) {
                    System.out.println(name + ": the render failed: " + e.getMessage());
                    continue;
                }
                Path out = OUTPUT.resolve("invoice-" + name + ".docx");
                result.saveToFile(out);
                System.out.println(name + ": wrote " + Path.of("java").resolve(out));
            }
        }
    }
}
