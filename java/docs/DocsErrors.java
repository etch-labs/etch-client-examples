// Code from the Java client page of the ETCH docs, section "Error handling".
// Run from the java folder with ./gradlew run -Pdocs=DocsErrors (it runs in the repository root).
import app.get_etch.client.ETCHClient;
import app.get_etch.client.exceptions.AuthenticationException;
import app.get_etch.client.exceptions.CapabilityNotLicensedException;
import app.get_etch.client.exceptions.ETCHException;
import app.get_etch.client.exceptions.NotFoundException;
import app.get_etch.client.exceptions.RateLimitException;
import app.get_etch.client.exceptions.ValidationException;
import java.util.Map;

public class DocsErrors {
    public static void main(String[] args) throws Exception {
        Map<String, Object> data = Map.of("customer", Map.of("name", "Example Ltd"));
        try (ETCHClient client = ETCHClient.create()) {
            // docs:start errors
            try {
                client.process("templates/invoice.docx", data);
            } catch (AuthenticationException e) {
                System.err.println("Check your API key: " + e.getMessage());
            } catch (CapabilityNotLicensedException e) {
                System.err.println("Not on your plan: " + e.getCapability());
            } catch (NotFoundException e) {
                System.err.println("Not found: " + e.getMessage());
            } catch (RateLimitException e) {
                System.err.println("Slow down; retry after " + e.getRetryAfter() + "s");
            } catch (ValidationException e) {
                System.err.println("Invalid request: " + e.getMessage());
            } catch (ETCHException e) {
                System.err.println("[" + e.getCode() + "] " + e.getMessage());
            }
            // docs:end errors
            System.out.println("Done");
        }
    }
}
