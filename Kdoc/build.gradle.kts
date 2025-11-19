import opekope2.optigui.buildscript.extension.Version

plugins {
    id("opekope2.optigui.buildscript.plugin.Dokka")
    alias(libs.plugins.fabric.loom) apply false
    alias(libs.plugins.moddev) apply false
}

version = Version.lazy(libs.versions.optigui)

base {
    archivesName = "kdoc"
}

repositories {
    mavenCentral()
}

dependencies {
    dokka(project(":OptiGUI"))
    dokka(project(":ScreenAPI"))
    dokka(project(":ScreenNBT"))
}

dokka {
    moduleName = "OptiGUI"
}
