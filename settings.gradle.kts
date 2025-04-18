pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    // Thanks gradle for pulling the wrong version of GSON
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    versionCatalogs {
        val libs by creating
    }
}

include(
    "OptiGUI",
    "ScreenAPI",
)
