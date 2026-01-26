import opekope2.optigui.buildscript.extension.Version

plugins {
    id("opekope2.optigui.buildscript.plugin.Common")
    id("opekope2.optigui.buildscript.plugin.Dokka")
    alias(libs.plugins.moddev)
}

version = Version.common(libs.versions.optigui, libs.versions.minecraft)

base {
    archivesName = "screen-nbt"
}

dependencies {
    api(project(":ScreenAPI"))
    api(project(":OptiGUI"))
}

neoForge {
    neoFormVersion = libs.versions.neoform.get()
    parchment {
        minecraftVersion = libs.versions.minecraft
        mappingsVersion = libs.versions.parchment
    }
}

dokka {
    moduleName = "Screen NBT"

    dokkaSourceSets.configureEach {
        perPackageOption {
            // language=RegExp
            matchingRegex = """opekope2\.optigui\.screen_nbt\.mixin"""
            suppress = true
            documentedVisibilities()
        }
    }
}
