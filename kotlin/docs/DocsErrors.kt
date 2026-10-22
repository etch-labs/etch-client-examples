// Code from the Kotlin client page of the ETCH docs, section "Error handling".
// Run from the kotlin folder with ./gradlew run -Pdocs=DocsErrors (it runs in the repository root).
// docs:start errors
import app.get_etch.client.ETCHClient
import app.get_etch.client.exceptions.AuthenticationException
import app.get_etch.client.exceptions.CapabilityNotLicensedException
import app.get_etch.client.exceptions.ETCHException
import app.get_etch.client.exceptions.NotFoundException
import app.get_etch.client.exceptions.RateLimitException
import app.get_etch.client.exceptions.ValidationException
// docs:end errors

fun main() {
    val data = mapOf("customer" to mapOf("name" to "Example Ltd"))
    ETCHClient.create().use { client ->
        // docs:start errors
        try {
            client.process(
                template = "templates/invoice.docx",
                data = data,
            )
        } catch (e: AuthenticationException) {
            System.err.println("Check your API key: ${e.message}")
        } catch (e: CapabilityNotLicensedException) {
            System.err.println("Not on your plan: ${e.capability}")
        } catch (e: NotFoundException) {
            System.err.println("Not found: ${e.message}")
        } catch (e: RateLimitException) {
            System.err.println("Slow down; retry after ${e.retryAfter}s")
        } catch (e: ValidationException) {
            System.err.println("Invalid request: ${e.message}")
        } catch (e: ETCHException) {
            System.err.println("[${e.errorCode}] ${e.message}")
        }
        // docs:end errors
        println("Done")
    }
}
