import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`
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
        exclusiveContent {
            forRepository { maven("https://mvn.runefox.dev/releases") { name = "Runefox" } }
            filter { includeGroup("dev.runefox") }
        }
        exclusiveContent {
            forRepository { maven("https://maven.sinytra.org") { name = "Sinytra" } }
            filter { includeGroup("org.sinytra"); includeGroup("org.sinytra.forgified-fabric-api") }
        }
        exclusiveContent {
            forRepository { maven("https://maven.wispforest.io/releases") { name = "Wisp Forest" } }
            filter { includeGroup("io.wispforest"); includeGroup("io.wispforest.endec") }
        }
    }

    dependencies {
        api(libs.jspecify)
    }

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        toolchain.languageVersion = libs.versions.java.map(JavaLanguageVersion::of)
    }

    tasks {
        withType<JavaCompile>().configureEach {
            sourceCompatibility = libs.versions.java.get()
            targetCompatibility = libs.versions.java.get()
            options.release = libs.versions.java.map(String::toInt)
            options.encoding = "UTF-8"
        }

        withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget = libs.versions.java.map(JvmTarget::fromTarget)
                freeCompilerArgs.addAll("-Xjvm-default=all", "-Xjsr305=strict")
            }
        }

        jar { withLicense() }

        named<Jar>("sourcesJar") { withLicense() }

        processResources {
            val properties = mapOf(
                "version" to version.toString(),
                "fabric_loader" to libs.versions.fabric.loader.get(),
                "fabric_api" to libs.versions.fabric.api.get(),
                "fabric_language_kotlin" to libs.versions.fabric.language.kotlin.get(),
                "neoforge" to libs.versions.neoforge.get(),
                "kotlin_for_forge" to libs.versions.kotlinforforge.get(),
                "minecraft" to libs.versions.minecraft.get(),
                "java" to libs.versions.java.get(),
                "owo_fabric" to libs.versions.owo.lib.fabric.get(),
                "owo_neoforge" to libs.versions.owo.lib.neoforge.get(),
            )
            val commentRegex = """^\s*//.*$""".toRegex()

            inputs.properties(properties)
            filesMatching("fabric.mod.json") { expand(properties) }
            filesMatching("META-INF/neoforge.mods.toml") { expand(properties) }
            filesMatching("*.mixins.json") { expand(properties) }
            filesMatching("assets/optigui/lang/*.jsonc") {
                name = name.removeSuffix("c")
                filter { line -> line.replace(commentRegex, "").takeIf { it != "" } }
            }
        }

        val codegen by registering
        named("compileJava") { dependsOn(codegen) }
        named("sourcesJar") { dependsOn(codegen) }
    }

    publishing.publications {
        register<MavenPublication>("maven") { from(components["java"]) }
    }

    afterEvaluate {
        publishing.publications {
            named<MavenPublication>("maven") { artifactId = base.archivesName.get() }
        }
    }
}
