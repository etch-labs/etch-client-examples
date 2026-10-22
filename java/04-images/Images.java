import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static app.get_etch.client.Images.imageFromFile;

/**
 * Adds a logo to the invoice data, then renders templates/invoice-with-logo.docx.
 *
 * <p>The template places the image with {@code {{#image logo width="56"}}}. An image in the data is
 * a data: URI, and {@code Images.imageFromFile} builds one from a file on disk
 * ({@code Images.imageFromBytes} does the same for bytes you already have).
 */
public class Images {

    private static final Path TEMPLATE = Path.of("../templates/invoice-with-logo.docx");
    private static final Path DATA = Path.of("../data/invoice.json");
    private static final Path LOGO = Path.of("../images/logo.png");
    private static final Path OUTPUT = Path.of("04-images", "output");

    public static void main(String[] args) throws Exception {
        Map<String, Object> data = new GsonBuilder()
                // Whole numbers stay whole (1, not 1.0), as in the JSON
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .fromJson(Files.readString(DATA), new TypeToken<Map<String, Object>>() {});
        data.put("logo", imageFromFile(LOGO));

        try (ETCHClient client = ETCHClient.create()) {
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), data);

            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("invoice-" + data.get("number") + "-with-logo.docx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
