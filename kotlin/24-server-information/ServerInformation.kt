import app.get_etch.client.ETCHClient
import app.get_etch.client.etchClientConfig
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

private const val CAPABILITY = "format.pdf"

/**
 * Checks the server and the licence before starting work.
 *
 * 1. Health: is the server up? The program stops here if it is not.
 * 2. Version: which ETCH server is answering.
 * 3. Licence: whose it is, its status and expiry, what it allows, and a check for one capability
 *    (PDF output) before relying on it.
 */
fun main() {
    // etchClientConfig reads ETCH_API_KEY, and ETCH_BASE_URL if you set it, as
    // ETCHClient.create() does. Keeping the configuration lets the message below name the address.
    val config = etchClientConfig()

    ETCHClient.create(config).use { client ->
        val health = client.healthcheck()
        if (health.status != "healthy") {
            System.err.println("The server at ${config.baseUrl} is not healthy: ${health.status}")
            exitProcess(1)
        }

        val version = client.version()
        println("Server: ${health.status}, version ${version.version}")

        val licence = client.licenseCapabilities()
        val expires = licence.expiresAt
            ?.let { DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneOffset.UTC).format(it) }
            ?: "never"
        println("Licence: ${licence.customer}, ${licence.status}, expires $expires")
        println("Capabilities: ${licence.licenseCapabilities.joinToString(", ")}")
        val pdf = if (licence.has(CAPABILITY)) "allowed" else "not in this licence"
        println("PDF output: $pdf")
    }
}
