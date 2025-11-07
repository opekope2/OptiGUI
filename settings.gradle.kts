pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }
        maven("https://maven.architectury.dev") { name = "Architectury" }
        maven("https://files.minecraftforge.net/maven") { name = "MinecraftForge" }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    // Thanks gradle for pulling the wrong version of GSON
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    versionCatalogs {
        val libs by creating
    }
}

include(
    "OptiGUI",
    "ScreenAPI",
    "ScreenNBT",
)
