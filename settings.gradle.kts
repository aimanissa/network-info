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
        mavenCentral()
    }
}

rootProject.name = "Network info"
include(
    ":app",
    ":data",
    ":domain",
    ":base:core",
    ":base:di",
    ":base:extensions",
    ":base:theme",
    ":base:navigation:api",
    ":base:navigation:internal",
    ":features:connection",
)
