import opekope2.optigui.buildscript.extension.Version
import opekope2.optigui.buildscript.task.GenerateI18nEnum
import opekope2.optigui.buildscript.task.GenerateInternalPackageInfos
import opekope2.optigui.buildscript.task.GenerateWorldNbtProvider
import opekope2.optigui.buildscript.task.VerifyChecksum

plugins {
    id("opekope2.optigui.buildscript.plugin.Common")
    id("opekope2.optigui.buildscript.plugin.Dokka")
    alias(libs.plugins.moddev)
    id("org.jetbrains.kotlin.jvm")
}

version = Version.common(libs.versions.optigui, libs.versions.minecraft)

val commonKotlin by configurations.registering { isCanBeResolved = false; isCanBeConsumed = true }

base {
    archivesName = "optigui"
}

dependencies {
    api(libs.ini4j)
    api(libs.runefox.json)
    api(libs.runefox.jsonkt)
    compileOnly(libs.cloth.config.neoforge) // fabric build is intermediary, neoforge build is mojmap

    api(project(":ScreenAPI"))
}

neoForge {
    neoFormVersion = libs.versions.neoform.get()
    parchment {
        minecraftVersion = libs.versions.minecraft
        mappingsVersion = libs.versions.parchment
    }
}

dokka {
    moduleName = "OptiGUI"

    dokkaSourceSets.configureEach {
        perPackageOption {
            // language=RegExp
            matchingRegex = """opekope2\.optigui\.(internal|mixin)(\..+)?"""
            suppress = true
            documentedVisibilities()
        }
    }
}

tasks {
    val generateI18n by registering(GenerateI18nEnum::class) {
        inputs.file(projectDir.resolve("src/main/resources/assets/optigui/lang/en_us.json"))
        enumPackage = "opekope2.optigui.internal"
    }

    val generateInternalPackageInfos by registering(GenerateInternalPackageInfos::class) {
        sourceRoot = projectDir.resolve("src/main/kotlin")
        packageMatcher("""^opekope2\.optigui\.internal(\..+)?$""")
    }

    val generateWorldNbtProvider by registering(GenerateWorldNbtProvider::class) {
        packageName = "opekope2.optigui.interaction.nbt_provider"
        minecraftClasspath.from(configurations.named("runtimeClasspath"))
    }

    val worldNbtProviderChecksum by registering(VerifyChecksum::class) {
        dependsOn(generateWorldNbtProvider)
        inputs.files(generateWorldNbtProvider)
        checksum("fbe22add93e1de5ce23f7d506fcf6d70")
    }

    codegen { dependsOn(generateI18n, generateInternalPackageInfos, generateWorldNbtProvider) }

    check { dependsOn(worldNbtProviderChecksum) }

    sourceSets.main {
        java.srcDirs(generateInternalPackageInfos)
        kotlin.srcDirs(generateI18n, generateWorldNbtProvider)
    }

    artifacts {
        commonJava(generateInternalPackageInfos)
    }
}

artifacts {
    sourceSets.main {
        kotlin.sourceDirectories.forEach { add("commonKotlin", it) }
    }
}
