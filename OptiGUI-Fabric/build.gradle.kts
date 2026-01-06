import opekope2.optigui.buildscript.extension.Version
import opekope2.optigui.buildscript.extension.commonProject
import opekope2.optigui.buildscript.task.GenerateInternalPackageInfos

plugins {
    id("opekope2.optigui.buildscript.plugin.Loader")
    alias(libs.plugins.fabric.loom)
    id("org.jetbrains.kotlin.jvm")
}

version = Version.fabric(libs.versions.optigui, libs.versions.minecraft)

val commonKotlin by configurations.registering { isCanBeResolved = true }

base {
    archivesName = "optigui-fabric"
}

repositories {
    exclusiveContent {
        forRepository { maven("https://maven.terraformersmc.com") { name = "Terraformers" } }
        filter { includeGroup("com.terraformersmc") }
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered { officialMojangMappings(); parchment(libs.parchment) })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.language.kotlin)
    modImplementation(libs.fabric.api)

    modApi(libs.cloth.config.fabric) { exclude(group = "net.fabricmc.fabric-api") }
    modImplementation(libs.modmenu)

    commonProject(":OptiGUI", kotlin = true)

    runtimeOnly(libs.ini4j)
    runtimeOnly(libs.runefox.json)
    runtimeOnly(libs.runefox.jsonkt)
    localRuntime(project(":ScreenAPI-Fabric", configuration = "namedElements"))
    localRuntime(project(":ScreenNBT-Fabric", configuration = "namedElements"))

    include(libs.ini4j)
    include(libs.runefox.json)
    include(libs.runefox.jsonkt)
    include(project(":ScreenAPI-Fabric"))
    include(project(":ScreenNBT-Fabric"))
}

loom {
    runtimeOnlyLog4j = true
    remapJsrAnnotationsToJetBrains = false // Unfuck MethodsReturnNonnullByDefault

    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("../run")
        }
    }
}

tasks {
    compileKotlin {
        dependsOn(commonKotlin, codegen)
        source(commonKotlin)
    }

    sourcesJar {
        dependsOn(commonKotlin)
        from(commonKotlin)
    }

    val generateInternalPackageInfos by registering(GenerateInternalPackageInfos::class) {
        sourceRoot = projectDir.resolve("src/main/kotlin")
        packageMatcher("""^opekope2\.optigui\.internal\.fabric(\..+)?$""")
    }

    codegen { dependsOn(generateInternalPackageInfos, ":OptiGUI:codegen") }

    sourceSets.main.get().java.srcDir(generateInternalPackageInfos)
    artifacts.commonJava(generateInternalPackageInfos)
}
