import app.get_etch.client.ETCHClient;
import app.get_etch.client.ETCHClientConfig;
import app.get_etch.client.models.HealthStatus;
import app.get_etch.client.models.LicenseCapabilities;
import app.get_etch.client.models.VersionInfo;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Checks the server and the licence before starting work.
 *
 * <ol>
 *   <li>Health: is the server up? The program stops here if it is not.
 *   <li>Version: which ETCH server is answering.
 *   <li>Licence: whose it is, its status and expiry, what it allows, and a check for one capability
 *       (PDF output) before relying on it.
 * </ol>
 */
public class ServerInformation {

    private static final String CAPABILITY = "format.pdf";

    public static void main(String[] args) throws Exception {
        // The builder reads ETCH_API_KEY, and ETCH_BASE_URL if you set it, as ETCHClient.create()
        // does. Keeping the configuration lets the message below name the address.
        ETCHClientConfig config = ETCHClientConfig.builder().build();

        try (ETCHClient client = ETCHClient.create(config)) {
            HealthStatus health = client.healthcheck();
            if (!"healthy".equals(health.getStatus())) {
                System.err.println("The server at " + config.getBaseUrl() + " is not healthy: "
                        + health.getStatus());
                System.exit(1);
            }

            VersionInfo version = client.version();
            System.out.println("Server: " + health.getStatus() + ", version "
                    + version.getVersion());

            LicenseCapabilities licence = client.licenseCapabilities();
            String expires = licence.getExpiresAt() == null
                    ? "never"
                    : DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneOffset.UTC)
                            .format(licence.getExpiresAt());
            System.out.println("Licence: " + licence.getCustomer() + ", " + licence.getStatus()
                    + ", expires " + expires);
            System.out.println("Capabilities: "
                    + String.join(", ", licence.getLicenseCapabilities()));
            String pdf = licence.has(CAPABILITY) ? "allowed" : "not in this licence";
            System.out.println("PDF output: " + pdf);
        }
    }
}
