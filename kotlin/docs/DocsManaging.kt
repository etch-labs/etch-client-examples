// Code from the Kotlin client page of the ETCH docs, section "Managing templates and folders".
// Run from the kotlin folder with ./gradlew run -Pdocs=DocsManaging
// (it runs in the repository root).
// docs:start manage
import app.get_etch.client.ETCHClient
import java.io.File
// docs:end manage

fun main() {
    ETCHClient.create().use { client ->
        // An earlier run may have left the folder behind; it must be empty to delete it.
        for (old in client.listFolders()) {
            if (old.path != "docs-example-invoices") {
                continue
            }
            for (t in client.listTemplates(old.path).templates) {
                client.deleteTemplate(t.id)
            }
            client.deleteFolder(old.id)
        }

        // docs:start manage
        val folder = client.createFolder("docs-example-invoices")

        val uploaded = client.uploadTemplate(
            File("templates/letter.md"), "letter.md",
            folder.path, listOf("letters"),
        )

        val (templates, total) = client.listTemplates(folder.path)
        println("$total template(s)")
        for (t in templates) {
            println("${t.name} ${t.id}")
        }

        client.deleteTemplate(uploaded.id)
        client.deleteFolder(folder.id)
        // docs:end manage
    }
}
