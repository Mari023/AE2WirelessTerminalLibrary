import net.minecraftforge.gradle.common.util.RunConfig

buildscript {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}

plugins {
    id("net.neoforged.gradle") version "[6.0.18,6.2)"
    id("org.spongepowered.mixin") version "0.7+"
    id("com.diffplug.spotless") version "6.21.0"
    id("maven-publish")
    java
    idea
}

val modVersion: String by project
val modloader: String by project
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
val forgeVersion: String by project
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
    add("minecraft", "net.neoforged:forge:${minecraftVersion}-${forgeVersion}")

    implementation(fg.deobf("top.theillusivec4.curios:curios-forge:${curiosVersion}"))
    implementation(fg.deobf("me.shedaniel.cloth:cloth-config-${modloader}:${clothVersion}"))
    implementation(fg.deobf("dev.architectury:architectury-${modloader}:${architecturyVersion}"))
    implementation(fg.deobf("appeng:appliedenergistics2-${modloader}:${ae2Version}") as ExternalModuleDependency) {
        exclude(group = "mezz.jei")
        exclude(group = "me.shedaniel")
    }

    compileOnly(fg.deobf("me.shedaniel:RoughlyEnoughItems-${modloader}:${reiVersion}"))
    compileOnly(fg.deobf("mezz.jei:jei-${jeiMinecraftVersion}-${modloader}:${jeiVersion}"))

    when (runtimeItemlistMod) {
        "rei" -> runtimeOnly(fg.deobf("me.shedaniel:RoughlyEnoughItems-${modloader}:${reiVersion}"))

        "jei" -> runtimeOnly(fg.deobf("mezz.jei:jei-${jeiMinecraftVersion}-${modloader}:${jeiVersion}"))

        "emi" -> {
            runtimeOnly(fg.deobf("dev.emi:emi-${modloader}:${emiVersion}+${minecraftVersion}"))
            runtimeOnly(fg.deobf("mezz.jei:jei-${jeiMinecraftVersion}-${modloader}:${jeiVersion}"))
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

minecraft {
    mappings("official", minecraftVersion)
    runs {
        val config = Action<RunConfig> {
            properties(mapOf(
                    "fml.earlyprogresswindow" to "false",
                    "forge.logging.console.level" to "debug",
                    "mixin.env.remapRefMap" to "true",
                    "mixin.env.refMapRemappingFile" to "${projectDir}/build/createSrgToMcp/output.srg"
            ))
            workingDirectory = project.file("run").canonicalPath
            source(sourceSets["main"])
        }

        create("client", config)
        create("server", config)
    }
}

mixin {
    add(sourceSets.main.get(), "ae2wtlib.mixins.refmap.json")
    config("ae2wtlib.mixins.json")
}

java {
    withSourcesJar()
}

tasks {
    jar {
        finalizedBy("reobfJar")
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
