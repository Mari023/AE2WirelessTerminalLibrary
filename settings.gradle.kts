rootProject.name = "ae2wtlib"
pluginManagement {
    plugins {
        id("net.neoforged.moddev") version "2.0.134"
        id("net.neoforged.moddev.repositories") version "2.0.134"
        id("com.diffplug.spotless") version "7.0.0.BETA2"
    }
}
plugins {
    id("net.neoforged.moddev.repositories")
}
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://modmaven.dev/")
            content {
                includeGroup("mezz.jei")
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
                includeGroup("dev.emi")
            }
        }
        maven {
            url = uri("https://maven.theillusivec4.top/")
            content {
                includeGroup("top.theillusivec4.curios")
            }
        }
        maven {
            url = uri("https://api.modrinth.com/maven")
            content {
                includeGroup("maven.modrinth")
            }
        }
    }
}

include("ae2wtlib_api")
