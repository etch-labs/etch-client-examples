import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Renders templates/quote.docx with the data in data/quote.json.
 *
 * <p>The template defines its signature block and contact line once as snippets and uses them
 * more than once, names values it reuses with #var, and keeps a note for the template's editor out
 * of the document with #exclude.
 */
public class ReusableBlocks {

    private static final Path TEMPLATE = Path.of("../templates/quote.docx");
    private static final Path DATA = Path.of("../data/quote.json");
    private static final Path OUTPUT = Path.of("11-reusable-blocks", "output");

    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // The client reads the data from the JSON file, so the data argument is null.
            ProcessOptions options = ProcessOptions.builder().dataFile(DATA).build();
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), null, options);

            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("quote.docx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
