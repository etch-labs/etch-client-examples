import app.get_etch.client.ETCHClient;
import app.get_etch.client.ETCHClientConfig;
import app.get_etch.client.Template;
import app.get_etch.client.exceptions.AuthenticationException;
import app.get_etch.client.exceptions.ETCHException;
import app.get_etch.client.exceptions.NotFoundException;
import app.get_etch.client.exceptions.ValidationException;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Makes three requests that fail, and handles each failure.
 *
 * <ol>
 *   <li>A wrong API key: AuthenticationException.
 *   <li>A stored template that does not exist: NotFoundException.
 *   <li>Data missing a field the template needs: ValidationException.
 * </ol>
 *
 * <p>Every client exception is an ETCHException, so one handler can report any failure: its code
 * (such as TEMPLATE_NOT_FOUND), the HTTP status and the message.
 */
public class ErrorHandling {

    private static final Path TEMPLATE = Path.of("../templates/invoice.docx");
    private static final Path DATA = Path.of("../data/invoice.json");

    interface Render {
        void run() throws ETCHException;
    }

    private static void attempt(String description, Render render) {
        System.out.println(description);
        try {
            render.run();
            System.out.println("  Rendered without an error");
        } catch (AuthenticationException e) {
            report(e, "Check the API key in ETCH_API_KEY.");
        } catch (NotFoundException e) {
            report(e, "Upload the template first, or check its name.");
        } catch (ValidationException e) {
            report(e, "Add the missing values to the data, or change the template.");
        } catch (ETCHException e) {
            report(e, "Something else went wrong; the code and message say what.");
        }
    }

    private static void report(ETCHException e, String advice) {
        System.out.println("  " + e.getClass().getSimpleName() + ": " + e.getCode()
                + " (HTTP " + e.getHttpStatus() + ")");
        System.out.println("  " + e.getMessage());
        System.out.println("  What to do: " + advice);
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> data = new GsonBuilder()
                // Whole numbers stay whole (1, not 1.0), as in the JSON
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .fromJson(Files.readString(DATA), new TypeToken<Map<String, Object>>() {});
        Map<String, Object> withoutCustomer = new HashMap<>(data);
        withoutCustomer.remove("customer");

        // The builder reads ETCH_BASE_URL as ETCHClient.create() does; only the key is replaced.
        ETCHClientConfig wrong = ETCHClientConfig.builder().apiKey("etch_not_a_real_key").build();
        try (ETCHClient wrongKey = ETCHClient.create(wrong)) {
            attempt("1. Rendering with a wrong API key",
                    () -> wrongKey.process(Template.ofFile(TEMPLATE), data));
        }

        try (ETCHClient client = ETCHClient.create()) {
            attempt("2. Rendering a stored template that does not exist",
                    () -> client.process(Template.stored("no-such-template.docx"), data));
            attempt("3. Rendering with data that has no customer",
                    () -> client.process(Template.ofFile(TEMPLATE), withoutCustomer));
        }
    }
}
