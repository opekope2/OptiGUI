import opekope2.optigui.buildscript.extension.Version
import opekope2.optigui.buildscript.extension.commonProject

plugins {
    id("opekope2.optigui.buildscript.plugin.Loader")
    alias(libs.plugins.moddev)
}

version = Version.neoForge(libs.versions.optigui, libs.versions.minecraft)

base {
    archivesName = "screen-api-neoforge"
}

dependencies {
    commonProject(":ScreenAPI")
}

neoForge {
    version = libs.versions.neoforge.get()
    parchment {
        minecraftVersion = libs.versions.minecraft
        mappingsVersion = libs.versions.parchment
    }
}
