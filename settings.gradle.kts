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

rootProject.name = "weather"
include(":app")
include(":data")
include(":domain")
include(":presentation:ui")
include(":presentation:model")
include(":presentation:weather")
include(":core:di")
include(":core:viewmodel")
include(":core:location")
include(":presentation:onboarding")
include(":analytics")
include(":analytics_dev:impl")
include(":analytics_dev:public")
