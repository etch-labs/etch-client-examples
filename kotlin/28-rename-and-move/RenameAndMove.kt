import app.get_etch.client.ETCHClient
import app.get_etch.client.Template
import app.get_etch.client.exceptions.ETCHException
import app.get_etch.client.models.FolderInfo
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import java.nio.file.Files
import java.nio.file.Path

private val TEMPLATE = Path.of("../templates/invoice.docx")
private val DATA = Path.of("../data/invoice.json")
private val OUTPUT = Path.of("28-rename-and-move", "output")

private const val TOP = "example-rename"
private const val ARCHIVE = "$TOP/archive"
private const val DRAFT = "invoice-draft.docx"
private const val NAME = "invoice.docx"

/** The folders under TOP, deepest first, so each is empty when its turn comes. */
private fun exampleFolders(client: ETCHClient): List<FolderInfo> =
    client.listFolders()
        .filter { it.path == TOP || it.path.startsWith("$TOP/") }
        .sortedByDescending { folder -> folder.path.count { it == '/' } }

/** Makes a change ETCH should refuse, and prints the error's code and HTTP status, and why. */
private fun refused(description: String, reason: String, change: () -> Unit) {
    try {
        change()
        println("$description: done")
    } catch (e: ETCHException) {
        println("$description: ${e.code} (HTTP ${e.httpStatus}). $reason")
    }
}

/**
 * Renames a stored template, moves it to another folder, and renders it at its new path.
 *
 * 1. Remove anything left under example-rename by an earlier run.
 * 2. Create the folder example-rename, and example-rename/archive inside it.
 * 3. Upload templates/invoice.docx into example-rename as invoice-draft.docx.
 * 4. Rename it to invoice.docx.
 * 5. Move it into example-rename/archive.
 * 6. Render it by its new path, example-rename/archive/invoice.docx.
 * 7. Try two changes ETCH refuses: a new extension, and a folder that does not exist.
 * 8. Delete the template and both folders.
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
        client.createFolder("archive", TOP)
        println("Created folder $ARCHIVE")

        val stored = client.uploadTemplate(TEMPLATE.toFile(), DRAFT, TOP)
        println("Uploaded ${stored.name} to ${stored.folder}")

        // Each change returns the template as it is now. Use the id from the result
        // for the next call: some servers give a renamed template a new id.
        val renamed = client.renameTemplate(stored.id, NAME)
        println("Renamed ${stored.name} to ${renamed.name}")

        val moved = client.moveTemplate(renamed.id, ARCHIVE)
        println("Moved ${moved.name} to ${moved.folder}")

        val path = "${moved.folder}/${moved.name}"
        val result = client.process(Template.stored(path), data)
        val out = OUTPUT.resolve("invoice-${data["number"]}.docx")
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")

        refused("Renaming ${moved.name} to invoice.pdf", "A template keeps its extension.") {
            client.renameTemplate(moved.id, "invoice.pdf")
        }
        refused("Moving ${moved.name} to $TOP/drafts", "Create the folder first.") {
            client.moveTemplate(moved.id, "$TOP/drafts")
        }

        client.deleteTemplate(moved.id)
        client.deleteFolder(ARCHIVE)
        client.deleteFolder(TOP)
        println("Deleted $NAME, $ARCHIVE and $TOP")
    }
}
