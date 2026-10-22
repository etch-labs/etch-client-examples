import app.get_etch.client.ETCHClient
import app.get_etch.client.Template
import app.get_etch.client.etchClientConfig
import app.get_etch.client.exceptions.AuthenticationException
import app.get_etch.client.exceptions.ETCHException
import app.get_etch.client.exceptions.NotFoundException
import app.get_etch.client.exceptions.ValidationException
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import java.nio.file.Files
import java.nio.file.Path

private val TEMPLATE = Path.of("../templates/invoice.docx")
private val DATA = Path.of("../data/invoice.json")

private fun attempt(description: String, render: () -> Unit) {
    println(description)
    try {
        render()
        println("  Rendered without an error")
    } catch (e: AuthenticationException) {
        report(e, "Check the API key in ETCH_API_KEY.")
    } catch (e: NotFoundException) {
        report(e, "Upload the template first, or check its name.")
    } catch (e: ValidationException) {
        report(e, "Add the missing values to the data, or change the template.")
    } catch (e: ETCHException) {
        report(e, "Something else went wrong; the code and message say what.")
    }
}

private fun report(e: ETCHException, advice: String) {
    println("  ${e.javaClass.simpleName}: ${e.code} (HTTP ${e.httpStatus})")
    println("  ${e.message}")
    println("  What to do: $advice")
}

/**
 * Makes three requests that fail, and handles each failure.
 *
 * 1. A wrong API key: AuthenticationException.
 * 2. A stored template that does not exist: NotFoundException.
 * 3. Data missing a field the template needs: ValidationException.
 *
 * Every client exception is an ETCHException, so one handler can report any failure: its code
 * (such as TEMPLATE_NOT_FOUND), the HTTP status and the message.
 */
fun main() {
    val data: Map<String, Any?> = GsonBuilder()
        // Whole numbers stay whole (1, not 1.0), as in the JSON
        .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
        .create()
        .fromJson(Files.readString(DATA), object : TypeToken<Map<String, Any?>>() {})
    val withoutCustomer = data - "customer"

    // etchClientConfig reads ETCH_BASE_URL as ETCHClient.create() does; only the key is given.
    val wrong = etchClientConfig(apiKey = "etch_not_a_real_key")
    ETCHClient.create(wrong).use { wrongKey ->
        attempt("1. Rendering with a wrong API key") {
            wrongKey.process(Template.ofFile(TEMPLATE), data)
        }
    }

    ETCHClient.create().use { client ->
        attempt("2. Rendering a stored template that does not exist") {
            client.process(Template.stored("no-such-template.docx"), data)
        }
        attempt("3. Rendering with data that has no customer") {
            client.process(Template.ofFile(TEMPLATE), withoutCustomer)
        }
    }
}
