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
        id("org.jetbrains.dokka") version "1.8.10" apply false
    }
}

include(
    "OptiGUI",
    "OptiGlue:1.18",
    "OptiGlue:1.18.2",
    "OptiGlue:1.19",
    "OptiGlue:1.19.3",
    "OptiGlue:1.19.4"
)
