import app.get_etch.client.ETCHClient;
import com.google.gson.Gson;

import java.nio.file.Files;
import java.nio.file.Path;

/** Renders templates/invoice.docx with the data in data/invoice.json. */
public class QuickStart {
    record Invoice(String number) {}

    public static void main(String[] args) throws Exception {
        var data = Path.of("../data/invoice.json");
        var invoice = new Gson().fromJson(Files.readString(data), Invoice.class);

        // The client reads ETCH_API_KEY, and ETCH_BASE_URL if you set it
        try (var client = ETCHClient.create()) {
            var result = client.process("../templates/invoice.docx", data);
            var out = Path.of("01-quick-start", "output", "invoice-" + invoice.number() + ".docx");
            Files.createDirectories(out.getParent());
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
