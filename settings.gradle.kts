pluginManagement {
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
    }
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "EduNav"
include(":app")
