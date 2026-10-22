// Code from the Java client page of the ETCH docs, section "Configuration".
// Run from the java folder with ./gradlew run -Pdocs=DocsConfiguration
// (it runs in the repository root).
// The client is only built here: the settings are examples.
import app.get_etch.client.ETCHClient;
import app.get_etch.client.ETCHClientConfig;
import java.time.Duration;

public class DocsConfiguration {
    public static void main(String[] args) throws Exception {
        String apiKey = System.getenv("ETCH_API_KEY");
        // docs:start config
        ETCHClientConfig config = ETCHClientConfig
            .builder("https://api.get-etch.app", apiKey)
            .timeout(Duration.ofMinutes(2))  // per attempt; no limit by default
            .maxRetries(5)                   // retries after the first attempt
            .addHeader("X-Request-Source", "nightly-run")
            .build();

        try (ETCHClient client = ETCHClient.create(config)) {
            // …
        }
        // docs:end config
        System.out.println("Client configured");
    }
}
