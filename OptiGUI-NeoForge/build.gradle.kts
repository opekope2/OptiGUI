import opekope2.optigui.buildscript.extension.Version
import opekope2.optigui.buildscript.extension.commonProject
import opekope2.optigui.buildscript.task.GenerateInternalPackageInfos

plugins {
    id("opekope2.optigui.buildscript.plugin.Loader")
    alias(libs.plugins.moddev)
    id("org.jetbrains.kotlin.jvm")
}

version = Version.neoForge(libs.versions.optigui, libs.versions.minecraft)

val commonKotlin by configurations.registering { isCanBeResolved = true }

base {
    archivesName = "optigui-neoforge"
}

repositories {
    exclusiveContent {
        forRepository { maven("https://thedarkcolour.github.io/KotlinForForge/") { name = "Kotlin for Forge" } }
        filter { includeGroup("thedarkcolour") }
    }
}

dependencies {
    implementation(libs.kotlinforforge) { exclude("net.neoforged.fancymodloader", "loader") }

    commonProject(":OptiGUI", kotlin = true)

    runtimeOnly(libs.ini4j)
    api(libs.owo.lib.neoforge)
    accessTransformers(libs.owo.lib.neoforge)
    interfaceInjectionData(libs.owo.lib.neoforge)
    runtimeOnly(libs.runefox.json)
    runtimeOnly(libs.runefox.jsonkt)
    runtimeOnly(project(":ScreenAPI-NeoForge"))
    runtimeOnly(project(":ScreenNBT-NeoForge"))

    jarJar(libs.ini4j)
    jarJar(libs.runefox.json)
    jarJar(libs.runefox.jsonkt)
    jarJar(project(":ScreenAPI-NeoForge"))
    jarJar(project(":ScreenNBT-NeoForge"))
}

neoForge {
    version = libs.versions.neoforge.get()
    parchment {
        minecraftVersion = libs.versions.minecraft
        mappingsVersion = libs.versions.parchment
    }

    runs {
        register("client") {
            client()
            ideName = "NeoForge Client ($path)"
            gameDirectory = file("../run")
        }
    }

    mods {
        register("optigui") {
            sourceSet(sourceSets["main"])
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
        // language=RegExp
        packageMatcher("""^opekope2\.optigui\.internal\.neoforge(\..+)?$""")
    }

    codegen { dependsOn(generateInternalPackageInfos, ":OptiGUI:codegen") }

    sourceSets.main.get().java.srcDir(generateInternalPackageInfos)
    artifacts.commonJava(generateInternalPackageInfos)
}
