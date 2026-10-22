import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

/**
 * Fetches the invoice data from a URL, then renders templates/invoice.docx.
 *
 * <p>Pass the URL as an argument
 * ({@code ./gradlew run -Pexample=03-data-from-url --args="<url>"}) or in INVOICE_DATA_URL. Any URL
 * that returns the JSON works, for example the raw URL of data/invoice.json in this repository. If
 * the URL needs a token, put it in INVOICE_DATA_TOKEN and it is sent as a bearer token.
 */
public class DataFromUrl {
    // Whole numbers stay whole (1, not 1.0), as in the JSON
    private static final Gson JSON = new GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create();

    private static final Path TEMPLATE = Path.of("../templates/invoice.docx");
    private static final Path OUTPUT = Path.of("03-data-from-url", "output");

    static Map<String, Object> fetchJson(String url, String token)
            throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json");
        if (token != null && !token.isBlank()) {
            request.header("Authorization", "Bearer " + token);
        }

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("GET " + url + " returned " + response.statusCode());
        }
        return JSON.fromJson(response.body(), new TypeToken<Map<String, Object>>() {});
    }

    public static void main(String[] args) throws Exception {
        String url = args.length > 0 ? args[0] : System.getenv("INVOICE_DATA_URL");
        if (url == null || url.isBlank()) {
            System.err.println(
                    "Pass the URL of the invoice JSON as an argument or in INVOICE_DATA_URL.");
            System.exit(1);
        }

        Map<String, Object> data = fetchJson(url, System.getenv("INVOICE_DATA_TOKEN"));

        try (ETCHClient client = ETCHClient.create()) {
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), data);

            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("invoice-" + data.get("number") + ".docx");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
