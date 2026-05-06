pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs.register("libs") { from(files("../gradle/libs.versions.toml")) }
}
