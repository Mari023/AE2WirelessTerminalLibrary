pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases")
        mavenCentral()
        maven {
            name = "Maven for PR #1"  // https://github.com/neoforged/ModDevGradle/pull/1
            url = uri("https://prmaven.neoforged.net/ModDevGradle/pr1")
            content {
                includeModule("net.neoforged.moddev", "net.neoforged.moddev.gradle.plugin")
                includeModule("net.neoforged.moddev.junit", "net.neoforged.moddev.junit.gradle.plugin")
                includeModule("net.neoforged", "moddev-gradle")
            }
        }
    }
}

rootProject.name = "ae2wtlib"
