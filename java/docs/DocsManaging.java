// Code from the Java client page of the ETCH docs, section "Managing templates and folders".
// Run from the java folder with ./gradlew run -Pdocs=DocsManaging (it runs in the repository root).
import app.get_etch.client.ETCHClient;
import app.get_etch.client.models.TemplateInfo;
import java.io.File;
import java.util.List;
// docs:start manage
import app.get_etch.client.models.FolderInfo;
import app.get_etch.client.models.TemplateList;
// docs:end manage

public class DocsManaging {
    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // An earlier run may have left the folder behind; it must be empty to delete it.
            for (FolderInfo old : client.listFolders()) {
                if (!old.getPath().equals("docs-example-invoices")) {
                    continue;
                }
                for (TemplateInfo t : client.listTemplates(old.getPath()).getTemplates()) {
                    client.deleteTemplate(t.getId());
                }
                client.deleteFolder(old.getId());
            }

            // docs:start manage
            FolderInfo folder = client.createFolder("docs-example-invoices");

            TemplateInfo uploaded = client.uploadTemplate(
                new File("templates/letter.md"), "letter.md",
                folder.getPath(), List.of("letters"));

            TemplateList listing = client.listTemplates(folder.getPath(), null, null);
            System.out.println(listing.getTotal() + " template(s)");
            for (TemplateInfo t : listing.getTemplates()) {
                System.out.println(t.getName() + " " + t.getId());
            }

            client.deleteTemplate(uploaded.getId());
            client.deleteFolder(folder.getId());
            // docs:end manage
        }
    }
}
