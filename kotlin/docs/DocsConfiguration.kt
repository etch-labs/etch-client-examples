// Code from the Kotlin client page of the ETCH docs, section "Configuration".
// Run from the kotlin folder with ./gradlew run -Pdocs=DocsConfiguration
// (it runs in the repository root).
// The client is only built here: the settings are examples.
// docs:start config
import app.get_etch.client.ETCHClient
import app.get_etch.client.etchClientConfig
import java.time.Duration
// docs:end config

fun main() {
    // docs:start config
    val config = etchClientConfig {
        baseUrl("https://api.get-etch.app")
        apiKey(System.getenv("ETCH_API_KEY"))
        timeout(Duration.ofMinutes(2))  // per attempt; no limit by default
        maxRetries(5)                   // retries after the first attempt
        addHeader("X-Request-Source", "nightly-run")
    }

    ETCHClient.create(config).use { client ->
        // …
    }
    // docs:end config
    println("Client configured")
}
