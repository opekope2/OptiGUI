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

repositories {}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn) { classifier("v2") })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.language.kotlin)

    modImplementation(fabric.events.interaction.v0)
    modImplementation(fabric.key.binding.api.v1)
    modImplementation(fabric.lifecycle.events.v1)
    modImplementation(fabric.networking.api.v1)
    modImplementation(fabric.resource.loader.v0)
    modImplementation(fabric.screen.api.v1)

    implementation(libs.apache.commons.text)
    include(libs.apache.commons.text)
    implementation(libs.ini4j)
    include(libs.ini4j)

    testImplementation(kotlin("test"))

    if (project.hasProperty("javaSyntax")) {
        dokkaPlugin(libs.dokka.plugin.java.syntax)
    }
}

loom {
    accessWidenerPath = file("src/main/resources/optigui.accesswidener")

    runtimeOnlyLog4j = true
}

tasks {
    val javaVersion = libs.versions.java.get()

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        options.release = javaVersion.toInt()
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion))
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
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

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(javaVersion)
        }
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
        withSourcesJar()
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "SKIPPED", "FAILED")
        }
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
                val fabricVersion = fabric.bom.get().version
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
