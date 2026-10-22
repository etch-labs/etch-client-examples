// Code from the Java client page of the ETCH docs, section "Server and licence".
// Run from the java folder with ./gradlew run -Pdocs=DocsServer (it runs in the repository root).
import app.get_etch.client.ETCHClient;
// docs:start server
import app.get_etch.client.models.HealthStatus;
import app.get_etch.client.models.LicenseCapabilities;
import app.get_etch.client.models.LicenseInfo;
// docs:end server

public class DocsServer {
    public static void main(String[] args) throws Exception {
        try (ETCHClient client = ETCHClient.create()) {
            // docs:start server
            HealthStatus health = client.healthcheck();
            System.out.println(health.getStatus() + ", server "
                + client.version().getVersion());

            LicenseInfo licence = client.licenseInfo();
            System.out.println(licence.getCustomer() + ", expires "
                + licence.getExpiresAt());

            LicenseCapabilities caps = client.licenseCapabilities();
            System.out.println("PDF output: " + caps.has("format.pdf"));
            // docs:end server
        }
    }
}
