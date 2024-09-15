pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
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

rootProject.name = "christian_calendar"

include(":app")
include(":feature")
include(":feature:month")
include(":feature:week")
include(":feature:day")
include(":feature:search")
include(":feature:settings")
include(":build-logic")
