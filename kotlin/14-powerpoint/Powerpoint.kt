import app.get_etch.client.ETCHClient
import java.nio.file.Files
import java.nio.file.Path

/**
 * Renders templates/project-update.pptx with the data in data/projects.json.
 *
 * The template is a PowerPoint deck. {{#slide-each projects}} turns its middle slide
 * into one slide per project, each with its own milestones and speaker notes, and a table row loop
 * on the last slide lists every project.
 */
fun main() {
    ETCHClient.create().use { client ->
        val template = "../templates/project-update.pptx"
        val data = Path.of("../data/projects.json")
        val result = client.process(template, data)
        val out = Path.of("14-powerpoint", "output", "project-update.pptx")
        Files.createDirectories(out.parent)
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
