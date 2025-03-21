pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        // Maven Central repository
        mavenCentral()

        // JitPack repository (for GitHub-based libraries)
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "TickoJet"
include(":app")
 