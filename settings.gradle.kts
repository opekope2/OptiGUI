pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0" // FIXME gradle is retarded
}

dependencyResolutionManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }
    }
    versionCatalogs {
        val libs by creating
        val fabric by creating {
            val fabricApiVersion: String by settings
            from("net.fabricmc.fabric-api:fabric-api-catalog:$fabricApiVersion")
        }
    }
}

include("OptiGUI")
