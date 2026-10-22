import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Renders templates/project-update.pptx with the data in data/projects.json.
 *
 * <p>The template is a PowerPoint deck. {@code {{#slide-each projects}}} turns its middle slide
 * into one slide per project, each with its own milestones and speaker notes, and a table row loop
 * on the last slide lists every project.
 */
public class Powerpoint {

    private static final Path TEMPLATE = Path.of("../templates/project-update.pptx");
    private static final Path DATA = Path.of("../data/projects.json");
    private static final Path OUTPUT = Path.of("14-powerpoint", "output");

    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // The client reads the data from the JSON file, so the data argument is null.
            ProcessOptions options = ProcessOptions.builder().dataFile(DATA).build();
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), null, options);

            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("project-update.pptx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
