import app.get_etch.client.ETCHClient
import app.get_etch.client.Template
import app.get_etch.client.models.FolderInfo
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import java.nio.file.Files
import java.nio.file.Path

private val TEMPLATE = Path.of("../templates/invoice.docx")
private val DATA = Path.of("../data/invoice.json")
private val OUTPUT = Path.of("26-folders", "output")

private const val TOP = "example-folders"
private const val INVOICES = "$TOP/invoices"
private const val NAME = "invoice.docx"

/** The folders under TOP, deepest first, so each is empty when its turn comes. */
private fun exampleFolders(client: ETCHClient): List<FolderInfo> =
    client.listFolders()
        .filter { it.path == TOP || it.path.startsWith("$TOP/") }
        .sortedByDescending { folder -> folder.path.count { it == '/' } }

/**
 * Keeps stored templates in folders, and renders one by its path.
 *
 * 1. Remove anything left under example-folders by an earlier run.
 * 2. Create the folder example-folders, and example-folders/invoices inside it.
 * 3. Upload templates/invoice.docx into example-folders/invoices.
 * 4. List the folders, and the templates in example-folders/invoices.
 * 5. Render the template by its path, example-folders/invoices/invoice.docx.
 * 6. Delete the template and both folders.
 */
fun main() {
    val data: Map<String, Any?> = GsonBuilder()
        // Whole numbers stay whole (1, not 1.0), as in the JSON
        .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
        .create()
        .fromJson(Files.readString(DATA), object : TypeToken<Map<String, Any?>>() {}.type)
    Files.createDirectories(OUTPUT)

    ETCHClient.create().use { client ->
        // A folder must be empty before it can be deleted.
        val leftovers = exampleFolders(client)
        for (folder in leftovers) {
            for (template in client.listTemplates(folder.path).templates) {
                client.deleteTemplate(template.id)
            }
            client.deleteFolder(folder.id)
        }
        if (leftovers.isNotEmpty()) {
            println("Removed $TOP left by an earlier run")
        }

        client.createFolder(TOP)
        println("Created folder $TOP")
        client.createFolder("invoices", TOP)
        println("Created folder $INVOICES")

        val stored = client.uploadTemplate(TEMPLATE.toFile(), NAME, INVOICES)
        println("Uploaded ${stored.name} to $INVOICES")

        println("Folders under $TOP:")
        for (folder in exampleFolders(client).sortedBy { it.path }) {
            println("  ${folder.path}, templates: ${folder.templateCount}")
        }

        val found = client.listTemplates(INVOICES)
        println("Templates in $INVOICES: ${found.total}")
        for (template in found.templates) {
            println("  ${template.name}")
        }

        // A stored template in a folder is rendered by its path.
        val result = client.process(Template.stored("$INVOICES/$NAME"), data)
        val out = OUTPUT.resolve("invoice-${data["number"]}.docx")
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")

        client.deleteTemplate(stored.id)
        client.deleteFolder(INVOICES)
        client.deleteFolder(TOP)
        println("Deleted $NAME, $INVOICES and $TOP")
    }
}
