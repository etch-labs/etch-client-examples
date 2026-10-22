import app.get_etch.client.ETCHClient
import com.google.gson.Gson
import java.nio.file.Files
import java.nio.file.Path

private data class Invoice(val number: String)

/** Renders templates/invoice.docx with the data in data/invoice.json. */
fun main() {
    val data = Path.of("../data/invoice.json")
    val invoice = Gson().fromJson(Files.readString(data), Invoice::class.java)

    // The client reads ETCH_API_KEY, and ETCH_BASE_URL if you set it
    ETCHClient.create().use { client ->
        val result = client.process("../templates/invoice.docx", data)
        val out = Path.of("01-quick-start", "output", "invoice-${invoice.number}.docx")
        Files.createDirectories(out.parent)
        result.saveToFile(out)
        println("Wrote ${Path.of("kotlin").resolve(out)}")
    }
}
