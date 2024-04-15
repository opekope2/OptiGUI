import opekope2.optigui.buildscript.task.GenerateResourcePack
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.Year

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.fabric.loom)
}

base {
    archivesName = "optigui"
}

version = libs.versions.optigui.get()
group = "opekope2.optigui"

val systemTest by sourceSets.creating {
    compileClasspath += sourceSets["main"].compileClasspath
    runtimeClasspath += sourceSets["main"].runtimeClasspath
}

repositories {}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn) { classifier("v2") })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.language.kotlin)

    modImplementation(fabricApi.module("fabric-events-interaction-v0", libs.versions.fabric.api.get()))
    modImplementation(fabricApi.module("fabric-key-binding-api-v1", libs.versions.fabric.api.get()))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", libs.versions.fabric.api.get()))
    modImplementation(fabricApi.module("fabric-networking-api-v1", libs.versions.fabric.api.get()))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", libs.versions.fabric.api.get()))

    implementation(libs.apache.commons.text)
    include(libs.apache.commons.text)
    implementation(libs.ini4j)
    include(libs.ini4j)

    testImplementation(kotlin("test"))

    "systemTestImplementation"(sourceSets["main"].output)

    if (project.hasProperty("javaSyntax")) {
        dokkaPlugin(libs.dokka.plugin.java.syntax)
    }
}

val optiFineTestZip = "OptiFineTest.zip"
val iniTestZip = "IniTest.zip"

loom {
    accessWidenerPath = file("src/main/resources/optigui.accesswidener")

    runtimeOnlyLog4j = true

    runs {
        val client by getting {
            ideConfigGenerated(true)
        }
        val systemTest by creating {
            inherit(client)
            source(systemTest)
            runDir("run/systemTest")
            configName = "System Test"
            property("optigui.tester.enabled")
            property(
                "optigui.tester.resource_packs",
                arrayOf("file/$optiFineTestZip", "file/$iniTestZip").joinToString(File.pathSeparator)
            )
        }
    }
}

val javaVersion = libs.versions.java.get()

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
    withSourcesJar()
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(javaVersion)
        freeCompilerArgs.add("-Xjvm-default=all")
    }
    target {
        val main by compilations.getting
        val systemTest by compilations.getting {
            associateWith(main)
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = javaVersion.toInt()
    }

    jar {
        from(rootDir.resolve("LICENSE"))
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to version as String,
                    "fabric_loader" to libs.versions.fabric.loader.get(),
                    "fabric_language_kotlin" to libs.versions.fabric.language.kotlin.get(),
                    "minecraft" to libs.versions.minecraft.get(),
                    "java" to javaVersion
                )
            )
        }
    }

    "processSystemTestResources"(ProcessResources::class) {
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to version as String,
                    "fabric_loader" to libs.versions.fabric.loader.get(),
                    "fabric_language_kotlin" to libs.versions.fabric.language.kotlin.get(),
                    "minecraft" to libs.versions.minecraft.get(),
                    "java" to javaVersion
                )
            )
        }
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "SKIPPED", "FAILED")
        }
    }

    val generateOptiFineTestResourcePack by registering(GenerateResourcePack::class) {
        into(project.layout.buildDirectory.dir("generated/testResourcePack/OptiFine").get())
        minecraftJar = loom.namedMinecraftJars.first()
    }

    val generateIniTestResourcePack by registering(GenerateResourcePack::class) {
        into(project.layout.buildDirectory.dir("generated/testResourcePack/INI").get())
        minecraftJar = loom.namedMinecraftJars.first()
    }

    val generateTestResourcePacks by registering {
        dependsOn(generateOptiFineTestResourcePack, generateIniTestResourcePack)
    }

    val systemTest by loom.runs.getting

    val packOptiFineTestResourcePack by registering(Zip::class) {
        dependsOn(generateOptiFineTestResourcePack)
        from(generateOptiFineTestResourcePack.get().destination)
        destinationDirectory = projectDir.resolve(systemTest.runDir).resolve("resourcepacks")
        archiveFileName = optiFineTestZip
        include { true }
        outputs.file(destinationDirectory.file(archiveFileName))
    }

    val packIniTestResourcePack by registering(Zip::class) {
        dependsOn(generateIniTestResourcePack)
        from(generateIniTestResourcePack.get().destination)
        destinationDirectory = projectDir.resolve(systemTest.runDir).resolve("resourcepacks")
        archiveFileName = iniTestZip
        include { true }
        outputs.file(destinationDirectory.file(archiveFileName))
    }

    val packTestResourcePacks by registering {
        dependsOn(packOptiFineTestResourcePack, packIniTestResourcePack)
    }

    val runSystemTest by getting {
        dependsOn(packTestResourcePacks)
    }

    clean {
        delete(packOptiFineTestResourcePack.get().outputs.files)
        delete(packIniTestResourcePack.get().outputs.files)
    }

    dokkaHtml {
        moduleName = "OptiGUI"
        moduleVersion = version as String
        outputDirectory = layout.buildDirectory.dir(
            if (project.hasProperty("javaSyntax")) "dokka/javaHtml"
            else "dokka/kotlinHtml"
        )

        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
            footerMessage =
                "© 2022-${Year.now().value} opekope2. OptiGUI is not an official Minecraft product. Not associated with or endorsed by Mojang Studios."
            customAssets = listOf(projectDir.resolve("logo-icon.svg"))
            separateInheritedMembers = true
        }

        dokkaSourceSets.configureEach {
            documentedVisibilities = setOf(
                DokkaConfiguration.Visibility.PUBLIC,
                DokkaConfiguration.Visibility.PROTECTED
            )

            sourceLink {
                localDirectory = projectDir.resolve("src/main/kotlin")
                remoteUrl = uri("https://github.com/opekope2/OptiGUI/tree/$version/OptiGUI/src/main/kotlin").toURL()
                remoteLineSuffix = "#L"
            }

            externalDocumentationLink {
                val mappingsVersion = libs.versions.yarn.get()
                url = uri("https://maven.fabricmc.net/docs/yarn-$mappingsVersion/").toURL()
                packageListUrl = uri("https://maven.fabricmc.net/docs/yarn-$mappingsVersion/element-list").toURL()
            }
            externalDocumentationLink {
                val fabricVersion = libs.versions.fabric.api.get()
                url = uri("https://maven.fabricmc.net/docs/fabric-api-$fabricVersion/").toURL()
                packageListUrl = uri("https://maven.fabricmc.net/docs/fabric-api-$fabricVersion/element-list").toURL()
            }
            externalDocumentationLink {
                url = uri("https://ini4j.sourceforge.net/apidocs/").toURL()
                packageListUrl = uri("https://ini4j.sourceforge.net/apidocs/package-list").toURL()
            }

            perPackageOption {
                matchingRegex = """opekope2\.optigui\.internal(\..*)?"""
                suppress = true
                documentedVisibilities = setOf()
            }
            perPackageOption {
                matchingRegex = """opekope2\.optigui\.mixin"""
                suppress = true
                documentedVisibilities = setOf()
            }

            // Apply these last, otherwise the other options get ignored
            // You don't want to know how many hours I spent on this...
            jdkVersion = libs.versions.java.get().toInt()
            languageVersion = libs.versions.kotlin
        }
    }
}
