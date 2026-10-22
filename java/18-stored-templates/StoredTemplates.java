import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;
import app.get_etch.client.models.TemplateInfo;
import app.get_etch.client.models.TemplateList;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static app.get_etch.client.Images.imageFromFile;

/**
 * Stores a template on ETCH, renders it by name, replaces it, downloads it and deletes it.
 *
 * <ol>
 *   <li>Remove any copy of the template left by an earlier run.
 *   <li>Upload templates/invoice.docx as example-invoice.docx.
 *   <li>List the stored templates that match the name.
 *   <li>Render the stored template by name.
 *   <li>Replace it with templates/invoice-with-logo.docx.
 *   <li>Render it by the same name again: the output now has the logo.
 *   <li>Download the stored template.
 *   <li>Delete it.
 * </ol>
 */
public class StoredTemplates {

    private static final Path TEMPLATE = Path.of("../templates/invoice.docx");
    private static final Path NEW_VERSION = Path.of("../templates/invoice-with-logo.docx");
    private static final Path DATA = Path.of("../data/invoice.json");
    private static final Path LOGO = Path.of("../images/logo.png");
    private static final Path OUTPUT = Path.of("18-stored-templates", "output");

    private static final String STORED_NAME = "example-invoice.docx";

    private static void render(ETCHClient client, Map<String, Object> data, String fileName)
            throws Exception {
        ProcessResult result = client.process(Template.stored(STORED_NAME), data);
        save(result, fileName);
    }

    private static void save(ProcessResult result, String fileName) throws IOException {
        Path out = OUTPUT.resolve(fileName);
        result.saveToFile(out);
        System.out.println("Wrote " + Path.of("java").resolve(out));
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> data = new GsonBuilder()
                // Whole numbers stay whole (1, not 1.0), as in the JSON
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .fromJson(Files.readString(DATA), new TypeToken<Map<String, Object>>() {});
        // Only the second version of the template shows the logo; the first ignores it.
        data.put("logo", imageFromFile(LOGO));
        String number = String.valueOf(data.get("number"));
        Files.createDirectories(OUTPUT);

        try (ETCHClient client = ETCHClient.create()) {
            for (TemplateInfo t : client.listTemplates(null, null, STORED_NAME).getTemplates()) {
                if (t.getName().equals(STORED_NAME)) {
                    client.deleteTemplate(t.getId());
                    System.out.println("Removed " + STORED_NAME + " left by an earlier run");
                }
            }

            TemplateInfo stored = client.uploadTemplate(TEMPLATE.toFile(), STORED_NAME);
            System.out.println("Uploaded " + stored.getName());

            // The first two arguments filter by folder and format; null means any.
            TemplateList found = client.listTemplates(null, null, STORED_NAME);
            System.out.println("Stored templates matching " + STORED_NAME + ": "
                    + found.getTotal());
            for (TemplateInfo t : found.getTemplates()) {
                System.out.println("  " + t.getName() + ", " + t.getSize() + " bytes");
            }

            render(client, data, "invoice-" + number + ".docx");

            client.updateTemplate(stored.getId(), NEW_VERSION.toFile());
            System.out.println("Replaced " + STORED_NAME + " with " + NEW_VERSION.getFileName());

            render(client, data, "invoice-" + number + "-with-logo.docx");

            save(client.downloadTemplate(stored.getId()), STORED_NAME);

            client.deleteTemplate(stored.getId());
            System.out.println("Deleted " + STORED_NAME);
        }
    }
}
