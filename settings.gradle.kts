pluginManagement {
    includeBuild("build-logic")
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
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "BaseArchitecture"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:designsystem")
include(":core:model")
include(":core:navigation")
include(":core:ui-api")
include(":core:ui-impl")
include(":core:network-api")
include(":core:network-impl")
include(":core:analytics-impl")
include(":core:analytics-api")
include(":core:database-api")
include(":core:database-impl")
include(":core:datastore-api")
include(":core:datastore-impl")
include(":core:notification-api")
include(":core:notification-impl")
include(":benchmark")
include(":feature:product-api")
include(":feature:product-impl")
include(":feature:product-impl-mvp")
include(":feature:product-impl-mvi")
include(":feature:product-impl-mvc")
include(":feature:product-impl-classicmvvm")
include(":feature:cart-api")
include(":feature:cart-impl")
include(":feature:cart-impl-mvp")
include(":feature:cart-impl-mvi")
include(":feature:cart-impl-mvc")
include(":feature:cart-impl-classicmvvm")
include(":feature:chat-api")
include(":feature:chat-impl")
include(":feature:chat-impl-mvp")
include(":feature:chat-impl-mvi")
include(":feature:chat-impl-mvc")
include(":feature:chat-impl-classicmvvm")
