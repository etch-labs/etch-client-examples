import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessOptions;
import app.get_etch.client.ProcessResult;
import app.get_etch.client.Template;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Renders a template written in the program instead of read from a file.
 *
 * <p>The template is Markdown: an order confirmation of the kind an application might send as an
 * email. Its name, confirmation.md, tells ETCH what format it is in, so the result is Markdown too.
 * The program prints the result and saves it to a file.
 */
public class InlineTemplates {

    private static final Path DATA = Path.of("../data/order.json");
    private static final Path OUTPUT = Path.of("21-inline-templates", "output");

    private static final String TEMPLATE = """
            # Order {{order.number}}

            Hello {{customer.name}},

            Thank you for your order of {{order.placed | dateFormat:"D MMMM YYYY"}}. \
            It travels in {{summary.Parcels}} parcels:

            {{#each shipments}}
            ## Parcel {{@index1}}, {{carrier}}

            Arrives {{arrives | dateFormat:"dddd D MMMM"}}.

            {{#each items}}
            - {{qty}} x {{name}}
            {{/each}}

            {{/each}}
            Delivering to {{customer.address}}.
            """;

    public static void main(String[] args) throws Exception {
        // The name gives the template's format; the bytes are the text above.
        Template confirmation =
                Template.ofBytes("confirmation.md", TEMPLATE.getBytes(StandardCharsets.UTF_8));

        try (ETCHClient client = ETCHClient.create()) {
            // The client reads the data from the JSON file, so the data argument is null.
            ProcessOptions options = ProcessOptions.builder().dataFile(DATA).build();
            ProcessResult result = client.process(confirmation, null, options);

            System.out.println(new String(result.getData(), StandardCharsets.UTF_8));
            Files.createDirectories(OUTPUT);
            Path out = OUTPUT.resolve("order-confirmation.md");
            result.saveToFile(out);
            System.out.println("Wrote " + Path.of("java").resolve(out));
        }
    }
}
