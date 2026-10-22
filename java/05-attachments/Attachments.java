import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Renders templates/product-sheet.docx with product pictures sent as attachments.
 *
 * <p>data/products.json names each product's picture file. Each file is added to an Attachments
 * collection, which returns a key; the key goes in the data where the template's
 * {@code {{#image photo}}} reads it. The pictures travel as separate parts of the request instead
 * of base64 text inside the JSON.
 */
public class Attachments {

    private static final Path TEMPLATE = Path.of("../templates/product-sheet.docx");
    private static final Path DATA = Path.of("../data/products.json");
    private static final Path IMAGES = Path.of("../images");
    private static final Path OUTPUT = Path.of("05-attachments", "output");

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        Map<String, Object> data = new GsonBuilder()
                // Whole numbers stay whole (1, not 1.0), as in the JSON
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .fromJson(Files.readString(DATA), new TypeToken<Map<String, Object>>() {});

        // The client's Attachments class, named in full because this class has the same name
        var attachments = new app.get_etch.client.Attachments();
        for (Map<String, Object> product : (List<Map<String, Object>>) data.get("products")) {
            Path picture = IMAGES.resolve((String) product.get("image"));
            product.put("photo", attachments.addFile(picture));
        }

        try (ETCHClient client = ETCHClient.create()) {
            ProcessOptions options = ProcessOptions.builder().attachments(attachments).build();
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), data, options);

            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("product-sheet.docx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
