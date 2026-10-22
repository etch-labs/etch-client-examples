// Code from the Java client page of the ETCH docs, section "Quick start".
// Run from the java folder with ./gradlew run -Pdocs=Main (it runs in the repository root).
// docs:start quick-start
import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;
import java.nio.file.Path;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        // The client reads your API key from ETCH_API_KEY, and the server from ETCH_BASE_URL
        // if you set it.
        try (ETCHClient client = ETCHClient.create()) {
            ProcessResult result = client.process(
                Template.ofFile(Path.of("templates/invoice.md")),
                Map.of("name", "Example Ltd",
                       "date", "2026-04-15",
                       "total", "99.99")
            );
            result.saveToFile("invoice-out.md");
            System.out.println("Saved invoice-out.md");
        }
    }
}
// docs:end quick-start
