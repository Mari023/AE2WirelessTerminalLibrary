import net.neoforged.gradle.dsl.common.runs.run.Run

plugins {
    id("net.neoforged.gradle.userdev") version "7.0.80"
    id("com.diffplug.spotless") version "6.21.0"
    id("maven-publish")
    java
    idea
}

val modVersion: String by project
val minecraftVersion: String by project
val clothVersion: String by project
val ae2Version: String by project
val architecturyVersion: String by project
val runtimeItemlistMod: String by project
val jeiMinecraftVersion: String by project
val jeiVersion: String by project
val reiVersion: String by project
val emiVersion: String by project
val neoforgeVersion: String by project
val curiosVersion: String by project
val mavenGroup: String by project
val archivesBaseName: String by project

version = "$modVersion-SNAPSHOT"

val pr = System.getenv("PR_NUMBER") ?: ""
if (pr != "") {
    version = "$modVersion+pr$pr"
}

val tag = System.getenv("TAG") ?: ""
if (tag != "") {
    version = tag
}

val artifactVersion = version

dependencies {
    implementation("net.neoforged:neoforge:${neoforgeVersion}")

    implementation("top.theillusivec4.curios:curios-neoforge:${curiosVersion}")
    implementation("appeng:appliedenergistics2-neoforge:${ae2Version}")

    compileOnly("me.shedaniel:RoughlyEnoughItems-neoforge:${reiVersion}")
    compileOnly("mezz.jei:jei-${jeiMinecraftVersion}-forge:${jeiVersion}")

    when (runtimeItemlistMod) {
        "rei" -> runtimeOnly("me.shedaniel:RoughlyEnoughItems-neoforge:${reiVersion}")

        "jei" -> runtimeOnly("mezz.jei:jei-${jeiMinecraftVersion}-forge:${jeiVersion}")

        "emi" -> {
            runtimeOnly("dev.emi:emi-neoforge:${emiVersion}+${minecraftVersion}")
            runtimeOnly("mezz.jei:jei-${jeiMinecraftVersion}-forge:${jeiVersion}")
        }
    }

    implementation("com.google.code.findbugs:jsr305:3.0.2")

    //testing
    //runtimeOnly(fg.deobf("maven.modrinth:aeinfinitybooster:1.20.1-1.0.0+20"))
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://modmaven.dev/")
        content {
            includeGroup("net.fabricmc.fabric-api")
            includeGroup("appeng")
            includeGroup("mezz.jei")
        }
    }
    maven {
        url = uri("https://maven.bai.lol")
        content {
            includeGroup("mcp.mobius.waila")
            includeGroup("lol.bai")
        }
    }
    maven {
        url = uri("https://maven.shedaniel.me/")
        content {
            includeGroup("me.shedaniel")
            includeGroup("me.shedaniel.cloth")
            includeGroup("dev.architectury")
        }
    }
    maven {
        url = uri("https://maven.terraformersmc.com/")
        content {
            includeGroup("com.terraformersmc")
            includeGroup("dev.emi")
        }
    }
    maven {
        url = uri("https://maven.ladysnake.org/releases")
        content {
            includeGroup("dev.onyxstudios.cardinal-components-api")
        }
    }
    maven {
        url = uri("https://maven.parchmentmc.net/")
        content {
            includeGroup("org.parchmentmc.data")
        }
    }
    maven {
        url = uri("https://maven.theillusivec4.top/")
        content {
            includeGroup("top.theillusivec4.curios")
        }
    }
    maven {
        url = uri("https://repo.spongepowered.org/maven")
        content {
            includeGroup("org.spongepowered")
        }
    }
    maven {
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks {
    processResources {
        val resourceTargets = "META-INF/mods.toml"

        val replaceProperties = mapOf(
            "version" to version as String, "ae2_version" to ae2Version
        )

        inputs.properties(replaceProperties)
        filesMatching(resourceTargets) {
            expand(replaceProperties)
        }
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
}

runs {
    val config = Action<Run> {
        workingDirectory = project.file("run")
        modSource(project.sourceSets.main.get())
    }

    create("client", config)
    create("server", config)
}

publishing {
    publications {
        create<MavenPublication>("ae2wtlib") {
            groupId = mavenGroup
            artifactId = archivesBaseName
            version = artifactVersion.toString()

            from(components["java"])
        }
    }
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    java {
        target("/src/*/java/**/*.java")

        endWithNewline()
        indentWithSpaces()
        removeUnusedImports()
        toggleOffOn()
        eclipse().configFile("codeformat/codeformat.xml")
        importOrderFile("codeformat/ae2wtlib.importorder")
    }
}
