import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import java.net.URL
import java.time.Year

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

base { archivesName.set(project.extra["archives_base_name"] as String) }

version = project.extra["mod_version"] as String
group = project.extra["maven_group"] as String

repositories {
    mavenCentral()
}

dependencies {
    api(project(":Api", configuration = "namedElements"))

    if (project.hasProperty("javaSyntax")) {
        dokkaPlugin("org.jetbrains.dokka", "kotlin-as-java-plugin", project.extra["dokka_version"] as String)
    }
}

tasks {
    val javaVersion = JavaVersion.toVersion((project.extra["java_version"] as String).toInt())

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
        from(rootDir.resolve("LICENSE")) {
            rename { "${it}_${base.archivesName.get()}" }
        }
        archiveBaseName.set("optigui")
        archiveClassifier.set("filters")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
        }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }

    dokkaHtmlPartial {
        moduleName.set("OptiGUI Filters")
        moduleVersion.set(version as String)
        outputDirectory.set(
            buildDir.resolve(
                if (project.hasProperty("javaSyntax")) "docs/partial/javaHtml"
                else "docs/partial/kotlinHtml"
            )
        )

        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
            footerMessage = "© 2022-${Year.now().value} opekope2"
            customAssets = listOf(rootDir.resolve("logo-icon.svg"))
            separateInheritedMembers = true
        }

        dokkaSourceSets.configureEach {
            includes.from(rootDir.resolve("modules.md"))

            documentedVisibilities.set(
                setOf(
                    DokkaConfiguration.Visibility.PUBLIC,
                    DokkaConfiguration.Visibility.PROTECTED
                )
            )

            sourceLink {
                localDirectory.set(projectDir.resolve("src/main"))
                remoteUrl.set(URL("https://github.com/opekope2/OptiGUI/tree/$version/Filters/src/main"))
                remoteLineSuffix.set("#L")
            }

            externalDocumentationLink {
                val mappingsVersion = project.extra["yarn_mappings"]
                url.set(URL("https://maven.fabricmc.net/docs/yarn-$mappingsVersion/"))
                packageListUrl.set(URL("https://maven.fabricmc.net/docs/yarn-$mappingsVersion/element-list"))
            }
            externalDocumentationLink {
                val fabricVersion = project.extra["fabric_version"]
                url.set(URL("https://maven.fabricmc.net/docs/fabric-api-$fabricVersion/"))
                packageListUrl.set(URL("https://maven.fabricmc.net/docs/fabric-api-$fabricVersion/element-list"))
            }

            // Apply these last, otherwise the other options get ignored
            // You don't want to know how many hours I spent on this...
            jdkVersion.set(project.extra["java_version"] as Int)
            languageVersion.set(System.getProperty("kotlin_version"))
        }
    }
}
