pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }
        mavenCentral()
        gradlePluginPortal()
    }
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
