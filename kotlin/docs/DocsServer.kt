// Code from the Kotlin client page of the ETCH docs, section "Server and licence".
// Run from the kotlin folder with ./gradlew run -Pdocs=DocsServer (it runs in the repository root).
import app.get_etch.client.ETCHClient

fun main() {
    ETCHClient.create().use { client ->
        // docs:start server
        val health = client.healthcheck()
        println("${health.status}, server ${client.version().version}")

        val licence = client.licenseInfo()
        println("${licence.customer}, expires ${licence.expiresAt}")

        val caps = client.licenseCapabilities()
        println("PDF output: ${caps.has("format.pdf")}")
        // docs:end server
    }
}
