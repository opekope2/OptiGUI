import opekope2.optigui.buildscript.extension.Version
import opekope2.optigui.buildscript.extension.commonProject

plugins {
    id("opekope2.optigui.buildscript.plugin.Loader")
    alias(libs.plugins.fabric.loom)
}

version = Version.fabric(libs.versions.optigui, libs.versions.minecraft)

base {
    archivesName = "screen-nbt-fabric"
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered { officialMojangMappings(); parchment(libs.parchment) })
    modImplementation(libs.fabric.loader)

    commonProject(":ScreenNBT")
}

loom {
    runtimeOnlyLog4j = true
    remapJsrAnnotationsToJetBrains = false // Unfuck MethodsReturnNonnullByDefault
}
