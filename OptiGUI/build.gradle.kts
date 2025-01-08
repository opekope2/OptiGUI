import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
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
    modImplementation(libs.fabric.api)

    implementation(libs.apache.commons.text)
    include(libs.apache.commons.text)
    implementation(libs.ini4j)
    include(libs.ini4j)

    testImplementation(kotlin("test"))
}

loom {
    runtimeOnlyLog4j = true
}

tasks {
    val javaVersion = libs.versions.java.get()

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        options.release = javaVersion.toInt()
    }

    withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(javaVersion)
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }

    jar {
        from(rootDir.resolve("COPYING"))
        from(rootDir.resolve("COPYING.LESSER"))
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to version as String,
                    "fabric_loader" to libs.versions.fabric.loader.get(),
                    "fabric_api" to libs.versions.fabric.api.get(),
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
}
