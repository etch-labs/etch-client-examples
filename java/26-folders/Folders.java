import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;
import app.get_etch.client.models.FolderInfo;
import app.get_etch.client.models.TemplateInfo;
import app.get_etch.client.models.TemplateList;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Keeps stored templates in folders, and renders one by its path.
 *
 * <ol>
 *   <li>Remove anything left under example-folders by an earlier run.
 *   <li>Create the folder example-folders, and example-folders/invoices inside it.
 *   <li>Upload templates/invoice.docx into example-folders/invoices.
 *   <li>List the folders, and the templates in example-folders/invoices.
 *   <li>Render the template by its path, example-folders/invoices/invoice.docx.
 *   <li>Delete the template and both folders.
 * </ol>
 */
public class Folders {

    private static final Path TEMPLATE = Path.of("../templates/invoice.docx");
    private static final Path DATA = Path.of("../data/invoice.json");
    private static final Path OUTPUT = Path.of("26-folders", "output");

    private static final String TOP = "example-folders";
    private static final String INVOICES = TOP + "/invoices";
    private static final String NAME = "invoice.docx";

    /** The folders under TOP, deepest first, so each is empty when its turn comes. */
    private static List<FolderInfo> exampleFolders(ETCHClient client) throws Exception {
        return client.listFolders().stream()
                .filter(f -> f.getPath().equals(TOP) || f.getPath().startsWith(TOP + "/"))
                .sorted(Comparator.comparingLong(Folders::depth).reversed())
                .toList();
    }

    private static long depth(FolderInfo folder) {
        return folder.getPath().chars().filter(c -> c == '/').count();
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> data = new GsonBuilder()
                // Whole numbers stay whole (1, not 1.0), as in the JSON
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .fromJson(Files.readString(DATA), new TypeToken<Map<String, Object>>() {});
        Files.createDirectories(OUTPUT);

        try (ETCHClient client = ETCHClient.create()) {
            // A folder must be empty before it can be deleted.
            List<FolderInfo> leftovers = exampleFolders(client);
            for (FolderInfo folder : leftovers) {
                for (TemplateInfo t : client.listTemplates(folder.getPath()).getTemplates()) {
                    client.deleteTemplate(t.getId());
                }
                client.deleteFolder(folder.getId());
            }
            if (!leftovers.isEmpty()) {
                System.out.println("Removed " + TOP + " left by an earlier run");
            }

            client.createFolder(TOP);
            System.out.println("Created folder " + TOP);
            client.createFolder("invoices", TOP);
            System.out.println("Created folder " + INVOICES);

            TemplateInfo stored = client.uploadTemplate(TEMPLATE.toFile(), NAME, INVOICES);
            System.out.println("Uploaded " + stored.getName() + " to " + INVOICES);

            System.out.println("Folders under " + TOP + ":");
            List<FolderInfo> byPath = exampleFolders(client).stream()
                    .sorted(Comparator.comparing(FolderInfo::getPath))
                    .toList();
            for (FolderInfo f : byPath) {
                System.out.println("  " + f.getPath() + ", templates: " + f.getTemplateCount());
            }

            TemplateList found = client.listTemplates(INVOICES);
            System.out.println("Templates in " + INVOICES + ": " + found.getTotal());
            for (TemplateInfo t : found.getTemplates()) {
                System.out.println("  " + t.getName());
            }

            // A stored template in a folder is rendered by its path.
            ProcessResult result = client.process(Template.stored(INVOICES + "/" + NAME), data);
            Path out = OUTPUT.resolve("invoice-" + data.get("number") + ".docx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));

            client.deleteTemplate(stored.getId());
            client.deleteFolder(INVOICES);
            client.deleteFolder(TOP);
            System.out.println("Deleted " + NAME + ", " + INVOICES + " and " + TOP);
        }
    }
}
