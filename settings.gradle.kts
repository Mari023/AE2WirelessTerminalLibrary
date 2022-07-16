pluginManagement {
    repositories {
        maven("https://server.bbkr.space/artifactory/libs-release/")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.spongepowered.org/maven")
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "net.minecraftforge.gradle") {
                useModule("${requested.id}:ForgeGradle:${requested.version}")
            }
        }
    }
}

rootProject.name = "ae2wtlib"
