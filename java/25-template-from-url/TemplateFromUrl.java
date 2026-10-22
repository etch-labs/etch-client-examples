import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Renders a template that lives at a URL rather than in a local file.
 *
 * <p>Pass the URL as an argument
 * ({@code ./gradlew run -Pexample=25-template-from-url --args="<url>"}) or in INVOICE_TEMPLATE_URL,
 * for example the raw URL of templates/invoice.docx in this repository. The client downloads the
 * template and sends it with the data from data/invoice.json. The download is a plain request: your
 * ETCH API key goes only to ETCH, never to the template's host.
 */
public class TemplateFromUrl {

    private static final Path DATA = Path.of("../data/invoice.json");
    private static final Path OUTPUT = Path.of("25-template-from-url", "output");

    public static void main(String[] args) throws Exception {
        String url = args.length > 0 ? args[0] : System.getenv("INVOICE_TEMPLATE_URL");
        if (url == null || url.isBlank()) {
            System.err.println(
                    "Pass the URL of the template as an argument or in INVOICE_TEMPLATE_URL.");
            System.exit(1);
        }

        try (ETCHClient client = ETCHClient.create()) {
            // The client downloads the template, and reads the data from the JSON file,
            // so the data argument is null.
            ProcessOptions options = ProcessOptions.builder().dataFile(DATA).build();
            ProcessResult result = client.process(Template.ofUrl(URI.create(url)), null, options);

            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("invoice.docx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
