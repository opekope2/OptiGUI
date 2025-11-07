import opekope2.optigui.buildscript.task.GenerateInternalPackageInfos

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

architectury {
    platformSetupLoomIde()
    fabric()
}

val common: Configuration by configurations.creating { isCanBeResolved = true; isCanBeConsumed = false }
val compileClasspath: Configuration by configurations.getting { extendsFrom(common) }
val runtimeClasspath: Configuration by configurations.getting { extendsFrom(common) }
val developmentFabric: Configuration by configurations.getting { extendsFrom(common) }

// Files in this configuration will be bundled into your mod using the Shadow plugin.
// Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
val shadowBundle: Configuration by configurations.creating { isCanBeResolved = true; isCanBeConsumed = false }

repositories {
    maven("https://maven.terraformersmc.com") { name = "Terraformers" }
}

dependencies {
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.language.kotlin)
    modImplementation(libs.fabric.api)

    modApi(libs.cloth.config.fabric) { exclude(group = "net.fabricmc.fabric-api") }
    modImplementation(libs.modmenu)
    include(libs.ini4j)

    localRuntime(project(":ScreenNBT", configuration = "namedElements"))
    include(project(":ScreenAPI"))
    common(project(":OptiGUI", configuration = "namedElements")) {
        exclude(group = "net.fabricmc", module = "fabric-loader")
    }
    shadowBundle(project(":OptiGUI", configuration = "transformProductionFabric")) {
        exclude(group = "net.fabricmc", module = "fabric-loader")
    }
}

tasks {
    shadowJar {
        configurations = listOf(shadowBundle)
        archiveClassifier = "dev-shadow"

        from(rootDir.resolve("COPYING"))
        from(rootDir.resolve("COPYING.LESSER"))
        from(rootDir.resolve("README.md"))
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile = shadowJar.get().archiveFile
        injectAccessWidener = true
        archiveClassifier = null

        from(rootDir.resolve("COPYING"))
        from(rootDir.resolve("COPYING.LESSER"))
        from(rootDir.resolve("README.md"))
    }

    sourcesJar {
        val commonSources = project(":OptiGUI").tasks.getByName<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })
    }

    val generateInternalPackageInfos by registering(GenerateInternalPackageInfos::class) {
        sourceRoot = projectDir.resolve("src/main/kotlin")
        matchPackages = """^opekope2\.optigui\.internal\.fabric(\..+)?$""".toRegex()
    }

    named("compileJava") { dependsOn(generateInternalPackageInfos) }
    named("sourcesJar") { dependsOn(generateInternalPackageInfos) }

    sourceSets.main {
        java.srcDir(generateInternalPackageInfos)
    }
}
