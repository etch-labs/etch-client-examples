plugins {
    application
}

tasks.withType<JavaCompile> {
    options.release = 17
    // Java 17 reads sources in the system encoding (US-ASCII in a plain Linux shell).
    options.encoding = "UTF-8"
    // Report calls to deprecated client methods by name, not only as a summary note.
    options.compilerArgs.add("-Xlint:deprecation")
}

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven("https://packages.get-etch.app/maven/") {
                // The package token comes from the ETCH_TOKEN environment variable, so it is never
                // in a file. Gradle needs it only when it downloads the client.
                credentials {
                    username = "token"
                    password = System.getenv("ETCH_TOKEN")
                }
            }
        }
        filter { includeGroup("app.get-etch") }
    }
}

dependencies {
    implementation("app.get-etch:etch-client:26.10.12")
    // Examples that read or change their data parse the JSON with Gson; the ETCH client
    // already depends on this version. The others pass the file to the client.
    implementation("com.google.code.gson:gson:2.10.1")
}

// Each numbered folder (01-quick-start, 02-...) holds one example. Its class is named after the
// folder without the number: 03-data-from-url contains DataFromUrl.java.
val exampleFolders = rootDir
    .listFiles { file -> file.isDirectory && file.name.matches(Regex("\\d\\d-.+")) }
    .orEmpty()
    .sortedBy { it.name }

sourceSets {
    main {
        // docs holds the code shown on the Java page of the ETCH docs (see docs/README.md)
        java.setSrcDirs(exampleFolders + file("docs"))
    }
}

fun classFor(folder: String) =
    folder.substringAfter('-').split('-')
        .joinToString("") { part -> part.replaceFirstChar { it.uppercase() } }

// Pick the example with -Pexample=<folder>, e.g. ./gradlew run -Pexample=03-data-from-url
application {
    mainClass = providers.gradleProperty("docs")
        .orElse(providers.gradleProperty("example").map { classFor(it) })
        .orElse(classFor("01-quick-start"))
}

// The docs code uses the page's paths (templates/, data/), which start at the repository root:
// ./gradlew run -Pdocs=DocsBatch
if (providers.gradleProperty("docs").isPresent) {
    tasks.named<JavaExec>("run") { workingDir = rootDir.parentFile }
}
