import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import java.net.URL
import java.time.Year

plugins {
    id("fabric-loom")
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

base {
    archivesName = project.gradleProperty("archives_base_name")
}

version = project.gradleProperty("mod_version")
group = project.gradleProperty("maven_group")

repositories {}

dependencies {
    minecraft("com.mojang", "minecraft", project.gradleProperty("minecraft_version"))
    mappings("net.fabricmc", "yarn", project.gradleProperty("yarn_mappings"), classifier = "v2")
    modImplementation("net.fabricmc", "fabric-loader", project.gradleProperty("loader_version"))
    modImplementation(
        "net.fabricmc", "fabric-language-kotlin", project.gradleProperty("fabric_language_kotlin_version")
    )

    modImplementation(fabricApi.module("fabric-events-interaction-v0", project.gradleProperty("fabric_version")))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", project.gradleProperty("fabric_version")))
    modImplementation(fabricApi.module("fabric-networking-api-v1", project.gradleProperty("fabric_version")))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", project.gradleProperty("fabric_version")))

    include(implementation("org.apache.commons", "commons-text", project.gradleProperty("apache_commons_text_version")))
    include(implementation("org.ini4j", "ini4j", project.gradleProperty("ini4j_version")))

    testImplementation(kotlin("test"))

    if (project.hasProperty("javaSyntax")) {
        dokkaPlugin("org.jetbrains.dokka", "kotlin-as-java-plugin", project.gradleProperty("dokka_version"))
    }
}

loom {
    accessWidenerPath.set(file("src/main/resources/optigui.accesswidener"))
}

tasks {
    val javaVersion = JavaVersion.toVersion(project.gradleProperty("java_version").toInt())

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = javaVersion.toString()
        }
    }

    jar {
        from(rootDir.resolve("LICENSE"))
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to version,
                    "fabric_loader" to project.gradleProperty("loader_version"),
                    "fabric_language_kotlin" to project.gradleProperty("fabric_language_kotlin_version"),
                    "minecraft" to project.gradleProperty("minecraft_version"),
                    "java" to project.gradleProperty("java_version")
                )
            )
        }
        filesMatching("*.mixins.json") {
            expand(mutableMapOf("java" to project.gradleProperty("java_version")))
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
        }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
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
                "© 2022-${Year.now().value} opekope2. ${project.gradleProperty("mojank_eula_compliance_footer")}"
            customAssets = listOf(projectDir.resolve("logo-icon.svg"))
            separateInheritedMembers = true
        }

        dokkaSourceSets.configureEach {
            documentedVisibilities = setOf(
                DokkaConfiguration.Visibility.PUBLIC,
                DokkaConfiguration.Visibility.PROTECTED
            )

            perPackageOption {
                matchingRegex = ".*internal.*"
                suppress = true
            }

            perPackageOption {
                matchingRegex = ".*mixin.*"
                suppress = true
            }

            sourceLink {
                localDirectory = projectDir.resolve("src/main/kotlin")
                remoteUrl = URL("https://github.com/opekope2/OptiGUI/tree/$version/OptiGUI/src/main/kotlin")
                remoteLineSuffix = "#L"
            }

            externalDocumentationLink {
                val mappingsVersion = project.extra["yarn_mappings"]
                url = URL("https://maven.fabricmc.net/docs/yarn-$mappingsVersion/")
                packageListUrl = URL("https://maven.fabricmc.net/docs/yarn-$mappingsVersion/element-list")
            }
            externalDocumentationLink {
                val fabricVersion = project.extra["fabric_version"]
                url = URL("https://maven.fabricmc.net/docs/fabric-api-$fabricVersion/")
                packageListUrl = URL("https://maven.fabricmc.net/docs/fabric-api-$fabricVersion/element-list")
            }
            externalDocumentationLink {
                url = URL("https://ini4j.sourceforge.net/apidocs/")
                packageListUrl = URL("https://ini4j.sourceforge.net/apidocs/package-list")
            }

            perPackageOption {
                matchingRegex = """opekope2\.optigui\.internal(.*)"""
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
            jdkVersion = project.extra["java_version"] as Int
            languageVersion = System.getProperty("kotlin_version")
        }
    }
}
