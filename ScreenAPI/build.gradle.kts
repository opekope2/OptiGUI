import opekope2.optigui.buildscript.extension.Version

plugins {
    id("opekope2.optigui.buildscript.plugin.Common")
    alias(libs.plugins.moddev)
}

version = Version.common(libs.versions.optigui, libs.versions.minecraft)

base {
    archivesName = "screen-api"
}

neoForge {
    neoFormVersion = libs.versions.neoform.get()
    parchment {
        minecraftVersion = libs.versions.minecraft
        mappingsVersion = libs.versions.parchment
    }
}
