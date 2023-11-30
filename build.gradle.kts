import net.neoforged.gradle.dsl.common.runs.run.Run

buildscript {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}

plugins {
    id("net.neoforged.gradle.userdev") version "7.0.45"
    id("com.diffplug.spotless") version "6.21.0"
    id("maven-publish")
    java
    idea
}

val modVersion: String by project
val modloader: String by project
val legacyModloader: String by project
val minecraftVersion: String by project
val parchmentMinecraftVersion: String by project
val parchmentVersion: String by project
val fabricLoaderVersion: String by project
val fabricApiVersion: String by project
val trinketsVersion: String by project
val ccaVersion: String by project
val clothVersion: String by project
val modMenuVersion: String by project
val ae2Version: String by project
val architecturyVersion: String by project
val runtimeItemlistMod: String by project
val jeiMinecraftVersion: String by project
val jeiVersion: String by project
val reiVersion: String by project
val emiVersion: String by project
val neoforgeVersion: String by project
val curiosVersion: String by project

version = "$modVersion-SNAPSHOT"

val pr = System.getenv("PR_NUMBER") ?: ""
if (pr != "") {
    version = "$modVersion+pr$pr"
}

val tag = System.getenv("TAG") ?: ""
if (tag != "") {
    if (!tag.contains(modloader)) {
        throw GradleException("Tags for the $modloader version should contain ${modloader}: $tag")
    }
    version = tag
}

dependencies {
    implementation("net.neoforged:neoforge:${neoforgeVersion}")

    implementation("top.theillusivec4.curios:curios-forge:${curiosVersion}")
    implementation("me.shedaniel.cloth:cloth-config-${modloader}:${clothVersion}")
    implementation("dev.architectury:architectury-${modloader}:${architecturyVersion}")
    implementation("maven.modrinth:ae2:${ae2Version}") {
        exclude(group = "mezz.jei")
        exclude(group = "me.shedaniel")
    }

    compileOnly("me.shedaniel:RoughlyEnoughItems-${modloader}:${reiVersion}")
    compileOnly("mezz.jei:jei-${jeiMinecraftVersion}-${legacyModloader}:${jeiVersion}")

    when (runtimeItemlistMod) {
        "rei" -> runtimeOnly("me.shedaniel:RoughlyEnoughItems-${modloader}:${reiVersion}")

        "jei" -> runtimeOnly("mezz.jei:jei-${jeiMinecraftVersion}-${legacyModloader}:${jeiVersion}")

        "emi" -> {
            runtimeOnly("dev.emi:emi-${legacyModloader}:${emiVersion}+${minecraftVersion}")
            runtimeOnly("mezz.jei:jei-${jeiMinecraftVersion}-${legacyModloader}:${jeiVersion}")
        }
    }

    annotationProcessor("org.spongepowered:mixin:0.8.4:processor")
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
    jar {
        manifest {
            attributes(mapOf(
                    "MixinConfigs" to "ae2wtlib.mixins.json"
            ))
        }
    }

    processResources {
        val resourceTargets = "META-INF/mods.toml"

        val replaceProperties = mapOf(
                "version" to version as String,
                "ae2_version" to ae2Version
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
