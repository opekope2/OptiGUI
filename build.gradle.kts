import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.loom) apply false
    alias(libs.plugins.architectury)
    alias(libs.plugins.shadow) apply false
}

architectury {
    minecraft = libs.versions.minecraft.get()
}

allprojects {
    val loaderSuffix = if (extra.has("loom.platform")) "${extra["loom.platform"]}." else ""
    val minecraftVersion = rootProject.libs.versions.minecraft.get()
    val modVersion = rootProject.libs.versions.optigui.get()
    group = "opekope2.optigui"
    version = "$modVersion+$loaderSuffix$minecraftVersion"
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")

    base {
        archivesName = "optigui"
    }

    repositories {
    }

    project.extensions.configure<LoomGradleExtensionAPI>("loom") {
        runtimeOnlyLog4j = true
    }

    dependencies {
        "minecraft"(rootProject.libs.minecraft)
        "mappings"(variantOf(rootProject.libs.yarn) { classifier("v2") })
        api(rootProject.libs.jspecify)
    }

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.toVersion(rootProject.libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(rootProject.libs.versions.java.get())
        toolchain {
            languageVersion = JavaLanguageVersion.of(rootProject.libs.versions.java.get())
        }
    }

    tasks {
        withType<JavaCompile>().configureEach {
            sourceCompatibility = rootProject.libs.versions.java.get()
            targetCompatibility = rootProject.libs.versions.java.get()
            options.release = rootProject.libs.versions.java.get().toInt()
            options.encoding = "UTF-8"
        }

        withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget = JvmTarget.fromTarget(rootProject.libs.versions.java.get())
                freeCompilerArgs.add("-Xjvm-default=all")
            }
        }

        jar {
            from(rootDir.resolve("COPYING"))
            from(rootDir.resolve("COPYING.LESSER"))
        }

        processResources {
            val properties = mapOf(
                "version" to version as String,
                "fabric_loader" to libs.versions.fabric.loader.get(),
                "fabric_api" to libs.versions.fabric.api.get(),
                "fabric_language_kotlin" to libs.versions.fabric.language.kotlin.get(),
                "minecraft" to libs.versions.minecraft.get(),
                "java" to libs.versions.java.get(),
                "cloth_config" to libs.versions.cloth.config.fabric.get(),
            )

            filesMatching("fabric.mod.json") {
                expand(properties)
            }
            filesMatching("*.mixins.json") {
                expand(properties)
            }
        }
    }
}
