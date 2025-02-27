pluginManagement {
    includeBuild("build-logic") {

    }

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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":feature")
include(":feature:month")
include(":feature:week")
include(":feature:day")
include(":feature:search")
include(":feature:settings")
include(":core")
include(":core:designsystem")
include(":core:ui")
include(":calendar")
include(":core:calendar")
include(":core:data")
include(":core:model")
include(":core:analytics")
