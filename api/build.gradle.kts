plugins {
    id("net.neoforged.moddev")
    id("com.diffplug.spotless")
    id("maven-publish")
    java
    idea
}

val ae2Version: String by project
val neoforgeVersion: String by project
val mavenGroup: String by project
val modID: String by project


version = "0.0.0-SNAPSHOT"

val pr = System.getenv("PR_NUMBER") ?: ""
if (pr != "") {
    version = "0.0.0-pr$pr"
}

val tag = System.getenv("TAG") ?: ""
if (tag != "") {
    version = tag
}

val artifactVersion = version

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

dependencies {
    implementation("appeng:appliedenergistics2:${ae2Version}")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
}

tasks {
    processResources {
        // Ensure the resources get re-evaluate when the version changes
        inputs.property("version", version)
        inputs.property("ae2_version", ae2Version)

        val replaceProperties = mapOf(
            "version" to version as String, "ae2_version" to ae2Version
        )

        inputs.properties(replaceProperties)
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(replaceProperties)
        }
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

neoForge {
    version = neoforgeVersion
    mods {
        create(modID) {
            sourceSet(sourceSets.main.get())
        }
    }
    runs {
        configureEach {
            gameDirectory.file("run")
            systemProperty("forge.logging.console.level", "debug")
        }

        create("api_client") {
            client()
        }
        create("api_server") {
            server()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>(modID) {
            groupId = mavenGroup
            artifactId = modID
            version = artifactVersion.toString()

            from(components["java"])
        }
    }
}

spotless {
    java {
        target("/src/**/java/**/*.java")

        endWithNewline()
        indentWithSpaces()
        removeUnusedImports()
        toggleOffOn()
        eclipse().configFile("../codeformat/codeformat.xml")
        importOrderFile("../codeformat/ae2wtlib.importorder")
    }
}