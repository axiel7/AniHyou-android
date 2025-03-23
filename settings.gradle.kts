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
        maven { setUrl("https://jitpack.io") }
    }
}

rootProject.name = "AniHyou"
include(":app")
include(":baselineprofile")
include(":core:base")
include(":core:common")
include(":core:network")
include(":core:resources")
include(":core:model")
include(":core:ui")
include(":core:domain")
include(":feature:widget")
include(":feature:activitydetails")
include(":feature:calendar")
include(":feature:characterdetails")
include(":feature:editmedia")
include(":feature:explore")
include(":feature:home")
include(":feature:login")
include(":feature:mediadetails")
include(":feature:notifications")
include(":feature:profile")
include(":feature:reviewdetails")
include(":feature:settings")
include(":feature:worker")
include(":feature:staffdetails")
include(":feature:studiodetails")
include(":feature:thread")
include(":feature:usermedialist")
include(":wearos")
