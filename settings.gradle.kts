pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        exclusiveContent {
            forRepository { maven("https://maven.fabricmc.net") { name = "Fabric" } }
            filter { includeGroup("net.fabricmc"); includeGroup("net.fabricmc.unpick"); includeGroup("fabric-loom") }
        }
    }
}

plugins {
    // Thanks gradle for pulling the wrong version of GSON
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    versionCatalogs.register("libs")
}

include(
    "OptiGUI",
    "OptiGUI-Fabric",
    "OptiGUI-NeoForge",
    "ScreenAPI",
    "ScreenAPI-Fabric",
    "ScreenAPI-NeoForge",
    "ScreenNBT",
    "ScreenNBT-Fabric",
    "ScreenNBT-NeoForge",
)
