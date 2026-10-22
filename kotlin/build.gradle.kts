plugins {
    kotlin("jvm") version "2.4.10"
    application
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
    // The examples that read values from their data parse the JSON with Gson; the ETCH client
    // already depends on this version.
    implementation("com.google.code.gson:gson:2.10.1")
    // DocsAsync uses coroutines; the client's own dependency is not on the compile classpath.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

// Each numbered folder (01-quick-start, 02-...) holds one example. Its file is named after the
// folder without the number, so 01-quick-start contains QuickStart.kt, whose main is QuickStartKt.
val exampleFolders = rootDir
    .listFiles { file -> file.isDirectory && file.name.matches(Regex("\\d\\d-.+")) }
    .orEmpty()
    .sortedBy { it.name }

sourceSets {
    main {
        // docs holds the code shown on the Java & Kotlin page of the ETCH docs (see docs/README.md)
        kotlin.setSrcDirs(exampleFolders + file("docs"))
    }
}

fun classFor(folder: String) =
    folder.substringAfter('-').split('-')
        .joinToString("") { part -> part.replaceFirstChar { it.uppercase() } } + "Kt"

// Pick the example with -Pexample=<folder>, e.g. ./gradlew run -Pexample=01-quick-start, or a docs
// file with -Pdocs=<Name>, e.g. ./gradlew run -Pdocs=DocsBatch (Name.kt, whose main is NameKt)
application {
    mainClass = providers.gradleProperty("docs").map { "${it}Kt" }
        .orElse(providers.gradleProperty("example").map { classFor(it) })
        .orElse(classFor("01-quick-start"))
}

// The docs code uses the page's paths (templates/, data/), which start at the repository root:
// ./gradlew run -Pdocs=DocsBatch
if (providers.gradleProperty("docs").isPresent) {
    tasks.named<JavaExec>("run") { workingDir = rootDir.parentFile }
}
