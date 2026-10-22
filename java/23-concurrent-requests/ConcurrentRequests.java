import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Renders several invoices at the same time, at most two at once.
 *
 * <p>Each invoice in data/invoices.json is its own request to templates/invoice.docx. A thread pool
 * of LIMIT threads runs them, so no more than LIMIT requests are in flight and a long list does not
 * run into the server's rate limit. The threads share one client.
 */
public class ConcurrentRequests {
    // Whole numbers stay whole (1, not 1.0), as in the JSON
    private static final Gson JSON = new GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create();

    private static final Path TEMPLATE = Path.of("../templates/invoice.docx");
    private static final Path DATA = Path.of("../data/invoices.json");
    private static final Path OUTPUT = Path.of("23-concurrent-requests", "output");

    private static final int LIMIT = 2;

    record Invoices(Map<String, Object> seller, List<Map<String, Object>> invoices) {}

    public static void main(String[] args) throws Exception {
        Invoices data = JSON.fromJson(Files.readString(DATA), new TypeToken<Invoices>() {});
        Files.createDirectories(OUTPUT);

        ExecutorService pool = Executors.newFixedThreadPool(LIMIT);
        try (ETCHClient client = ETCHClient.create()) {
            List<Callable<Void>> renders = new ArrayList<>();
            for (Map<String, Object> invoice : data.invoices()) {
                renders.add(() -> {
                    Map<String, Object> request = new HashMap<>(invoice);
                    request.put("seller", data.seller());
                    ProcessResult result = client.process(Template.ofFile(TEMPLATE), request);
                    Path out = OUTPUT.resolve("invoice-" + invoice.get("number") + ".docx");
                    result.saveToFile(out);
                    System.out.println("Wrote " + Path.of("java").resolve(out));
                    return null;
                });
            }

            long started = System.nanoTime();
            for (Future<Void> render : pool.invokeAll(renders)) {
                render.get(); // rethrows a failed render's exception
            }
            double elapsed = (System.nanoTime() - started) / 1e9;
            System.out.printf("Rendered %d invoices in %.1f seconds, up to %d at a time%n",
                    data.invoices().size(), elapsed, LIMIT);
        } finally {
            pool.shutdown();
        }
    }
}
