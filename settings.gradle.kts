pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("fabric-loom") version settings.extra["loom_version"] as String apply false
        kotlin("jvm") version System.getProperty("kotlin_version") apply false
        id("net.kyori.blossom") version "1.3.1" apply false
    }
}

include(
    "OptiGUI",
)
