import app.get_etch.client.ETCHClient;
import app.get_etch.client.ETCHClientConfig;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;
import app.get_etch.client.exceptions.TimeoutException;
import app.get_etch.client.models.OutputFormat;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

/**
 * Configures the client: where it connects, how long it waits and how often it retries.
 *
 * <p>The API key and the server address come from the environment, never from the code. Two
 * clients are built:
 *
 * <ol>
 *   <li>One for normal use: up to two minutes to read a response, retried up to three times. It
 *       renders the invoice to PDF.
 *   <li>One that gives up quickly: 50 milliseconds and no retries. The same render takes longer
 *       than that, so it fails with a TimeoutException.
 * </ol>
 */
public class Configuration {

    private static final Path TEMPLATE = Path.of("../templates/invoice.docx");
    private static final Path DATA = Path.of("../data/invoice.json");
    private static final Path OUTPUT = Path.of("20-configuration", "output");

    public static void main(String[] args) throws Exception {
        Map<String, Object> data = new GsonBuilder()
                // Whole numbers stay whole (1, not 1.0), as in the JSON
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .fromJson(Files.readString(DATA), new TypeToken<Map<String, Object>>() {});

        // builder() reads ETCH_API_KEY, and ETCH_BASE_URL if you set it. The rendered document
        // arrives once the server has finished, so the read timeout is the one that limits how
        // long a render may take.
        ETCHClientConfig normal = ETCHClientConfig.builder()
                .readTimeout(Duration.ofMinutes(2))
                .maxRetries(3) // retries after the first attempt
                .build();
        System.out.println("Connecting to " + normal.getBaseUrl());
        ProcessOptions pdf = ProcessOptions.builder().outputFormat(OutputFormat.PDF).build();
        try (ETCHClient client = ETCHClient.create(normal)) {
            ProcessResult result = client.process(Template.ofFile(TEMPLATE), data, pdf);
            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("invoice-" + data.get("number") + ".pdf");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }

        ETCHClientConfig quickConfig = ETCHClientConfig.builder()
                .readTimeout(Duration.ofMillis(50))
                .maxRetries(0)
                .build();
        try (ETCHClient quick = ETCHClient.create(quickConfig)) {
            long started = System.nanoTime();
            try {
                quick.process(Template.ofFile(TEMPLATE), data, pdf);
                System.out.println("The quick client finished in time");
            } catch (TimeoutException e) {
                double waited = (System.nanoTime() - started) / 1e9;
                System.out.printf("The quick client gave up after %.2f seconds: %s%n",
                        waited, e.getCode());
            }
        }
    }
}
