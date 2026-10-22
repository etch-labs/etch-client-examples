// Code from the Java client page of the ETCH docs, section "Templates".
// Run from the java folder with ./gradlew run -Pdocs=DocsTemplates
// (it runs in the repository root).
import app.get_etch.client.ETCHClient;
import app.get_etch.client.ProcessResult;
import java.nio.file.Path;
import java.util.Map;
// docs:start templates
import app.get_etch.client.Template;
import app.get_etch.client.models.TemplateInfo;
import java.io.File;
import java.nio.charset.StandardCharsets;
// docs:end templates

public class DocsTemplates {
    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // docs:start templates
            Map<String, Object> data = Map.of("name", "Example Ltd");

            // A file on disk, uploaded with the request
            ProcessResult fromFile = client.process(
                Template.ofFile(Path.of("templates/letter.md")), data);

            // Template text you already have in memory; the name's extension
            // gives the format
            byte[] text = "Hello, {{name}}!".getBytes(StandardCharsets.UTF_8);
            ProcessResult fromBytes = client.process(
                Template.ofBytes("hello.md", text), data);

            // A template stored on your account. Upload it once:
            TemplateInfo stored = client.uploadTemplate(
                new File("templates/letter.md"), "letter.md");
            ProcessResult fromStored = client.process(
                Template.stored("letter.md"), data);
            // docs:end templates
            System.out.println(fromFile.getData().length + ", " + fromBytes.getData().length
                + " and " + fromStored.getData().length + " bytes");
            client.deleteTemplate(stored.getId());
        }
    }
}
