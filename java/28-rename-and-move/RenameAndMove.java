import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;
import app.get_etch.client.exceptions.ETCHException;
import app.get_etch.client.models.FolderInfo;
import app.get_etch.client.models.TemplateDetails;
import app.get_etch.client.models.TemplateInfo;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Renames a stored template, moves it to another folder, and renders it at its new path.
 *
 * <ol>
 *   <li>Remove anything left under example-rename by an earlier run.
 *   <li>Create the folder example-rename, and example-rename/archive inside it.
 *   <li>Upload templates/invoice.docx into example-rename as invoice-draft.docx.
 *   <li>Rename it to invoice.docx.
 *   <li>Move it into example-rename/archive.
 *   <li>Render it by its new path, example-rename/archive/invoice.docx.
 *   <li>Try two changes ETCH refuses: a new extension, and a folder that does not exist.
 *   <li>Delete the template and both folders.
 * </ol>
 */
public class RenameAndMove {

    private static final Path TEMPLATE = Path.of("../templates/invoice.docx");
    private static final Path DATA = Path.of("../data/invoice.json");
    private static final Path OUTPUT = Path.of("28-rename-and-move", "output");

    private static final String TOP = "example-rename";
    private static final String ARCHIVE = TOP + "/archive";
    private static final String DRAFT = "invoice-draft.docx";
    private static final String NAME = "invoice.docx";

    /** The folders under TOP, deepest first, so each is empty when its turn comes. */
    private static List<FolderInfo> exampleFolders(ETCHClient client) throws Exception {
        return client.listFolders().stream()
                .filter(f -> f.getPath().equals(TOP) || f.getPath().startsWith(TOP + "/"))
                .sorted(Comparator.comparingLong(RenameAndMove::depth).reversed())
                .toList();
    }

    private static long depth(FolderInfo folder) {
        return folder.getPath().chars().filter(c -> c == '/').count();
    }

    /** Makes a change ETCH should refuse, and prints the error's code and HTTP status, and why. */
    private static void refused(String description, Callable<?> change, String reason)
            throws Exception {
        try {
            change.call();
            System.out.println(description + ": done");
        } catch (ETCHException e) {
            System.out.println(description + ": " + e.getCode() + " (HTTP " + e.getHttpStatus()
                    + "). " + reason);
        }
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
            client.createFolder("archive", TOP);
            System.out.println("Created folder " + ARCHIVE);

            TemplateInfo stored = client.uploadTemplate(TEMPLATE.toFile(), DRAFT, TOP);
            System.out.println("Uploaded " + stored.getName() + " to " + stored.getFolder());

            // Each change returns the template as it is now. Use the id from the result
            // for the next call: some servers give a renamed template a new id.
            TemplateDetails renamed = client.renameTemplate(stored.getId(), NAME);
            System.out.println("Renamed " + stored.getName() + " to " + renamed.getName());

            TemplateDetails moved = client.moveTemplate(renamed.getId(), ARCHIVE);
            System.out.println("Moved " + moved.getName() + " to " + moved.getFolder());

            String path = moved.getFolder() + "/" + moved.getName();
            ProcessResult result = client.process(Template.stored(path), data);
            Path out = OUTPUT.resolve("invoice-" + data.get("number") + ".docx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));

            refused("Renaming " + moved.getName() + " to invoice.pdf",
                    () -> client.renameTemplate(moved.getId(), "invoice.pdf"),
                    "A template keeps its extension.");
            refused("Moving " + moved.getName() + " to " + TOP + "/drafts",
                    () -> client.moveTemplate(moved.getId(), TOP + "/drafts"),
                    "Create the folder first.");

            client.deleteTemplate(moved.getId());
            client.deleteFolder(ARCHIVE);
            client.deleteFolder(TOP);
            System.out.println("Deleted " + NAME + ", " + ARCHIVE + " and " + TOP);
        }
    }
}
