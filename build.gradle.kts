import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.fabric.loom) apply false
    alias(libs.plugins.moddev) apply false
}

allprojects {
    group = "dev.opekope2.optigui"
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

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
