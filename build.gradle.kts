import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URLEncoder
import java.time.Year

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.fabric.loom) apply false
    alias(libs.plugins.moddev) apply false
}

allprojects {
    group = "dev.opekope2.optigui"
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.dokka")

    val libs = rootProject.libs

    fun AbstractCopyTask.withLicense() {
        val licenseFile = projectDir.resolve("LICENSE")
        if (licenseFile.isFile) from(licenseFile)
        else from(rootDir.resolve("COPYING"), rootDir.resolve("COPYING.LESSER"))
    }

    repositories {
        mavenCentral()
        // https://docs.gradle.org/current/userguide/declaring_repositories.html#declaring_content_exclusively_found_in_one_repository
        exclusiveContent {
            forRepository { maven("https://maven.parchmentmc.org/") { name = "ParchmentMC" } }
            filter { includeGroup("org.parchmentmc.data") }
        }
        exclusiveContent {
            forRepository { maven("https://repo.spongepowered.org/repository/maven-public") { name = "Sponge" } }
            filter { includeGroupAndSubgroups("org.spongepowered") }
        }
        exclusiveContent {
            forRepository { maven("https://maven.shedaniel.me") { name = "Shedaniel" } }
            filter { includeGroup("me.shedaniel.cloth") }
        }
    }

    dependencies {
        api(libs.jspecify)
    }

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        toolchain.languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }

    // TODO move to buildSrc when gradle shits itself
    dokka {
        moduleName = name

        pluginsConfiguration.html {
            footerMessage = "© 2022-${Year.now().value} opekope2"
            customAssets.from(rootDir.resolve("assets/logo-icon.svg"))
            separateInheritedMembers = true
        }

        dokkaSourceSets.configureEach {
            documentedVisibilities(VisibilityModifier.Public, VisibilityModifier.Protected)

            val tag = URLEncoder.encode(version as String, Charsets.UTF_8)
            val baseUrl = "https://github.com/opekope2/OptiGUI/tree/$tag/${project.name}"
            sourceLink {
                localDirectory = projectDir.resolve("src/main/java")
                remoteUrl("$baseUrl/src/main/java")
                remoteLineSuffix = "#L"
            }
            sourceLink {
                localDirectory = projectDir.resolve("src/main/kotlin")
                remoteUrl("$baseUrl/src/main/kotlin")
                remoteLineSuffix = "#L"
            }

            perPackageOption {
                // language=RegExp
                matchingRegex = """opekope2\.optigui(\.screen_api|\.screen_nbt)?\.(internal|mixin)(\..+)?"""
                suppress = true
                documentedVisibilities()
            }

            // Apply these last, otherwise the other options get ignored
            // You don't want to know how many hours I spent on this...
            jdkVersion = libs.versions.java.map(String::toInt)
            languageVersion = libs.versions.kotlin
        }

        afterEvaluate {
            moduleVersion = version as String
        }
    }

    tasks {
        withType<JavaCompile>().configureEach {
            sourceCompatibility = libs.versions.java.get()
            targetCompatibility = libs.versions.java.get()
            options.release = libs.versions.java.get().toInt()
            options.encoding = "UTF-8"
        }

        withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget = JvmTarget.fromTarget(libs.versions.java.get())
                freeCompilerArgs.addAll("-Xjvm-default=all", "-Xjsr305=strict")
            }
        }

        jar { withLicense() }

        named<Jar>("sourcesJar") { withLicense() }

        register<Jar>("javadocJar") {
            dependsOn(dokkaGeneratePublicationHtml)
            from(dokkaGeneratePublicationHtml)
            archiveClassifier = "javadoc"
        }

        processResources {
            val properties = mapOf(
                "version" to version as String,
                "fabric_loader" to libs.versions.fabric.loader.get(),
                "fabric_api" to libs.versions.fabric.api.get(),
                "fabric_language_kotlin" to libs.versions.fabric.language.kotlin.get(),
                "minecraft" to libs.versions.minecraft.get(),
                "java" to libs.versions.java.get(),
                "cloth_config" to libs.versions.cloth.config.get(),
            )

            inputs.properties(properties)
            filesMatching("fabric.mod.json") { expand(properties) }
            filesMatching("*.mixins.json") { expand(properties) }
        }

        val codegen by registering
        named("compileJava") { dependsOn(codegen) }
        named("sourcesJar") { dependsOn(codegen) }
    }

    afterEvaluate {
        publishing.publications {
            register<MavenPublication>("maven") {
                artifactId = base.archivesName.get()
                from(components["java"])
            }
        }
    }
}
