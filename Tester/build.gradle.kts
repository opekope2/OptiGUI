plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
}

base {
    archivesName = "optigui-tester"
}

version = libs.versions.optigui.get()
group = "opekope2.optigui"

repositories {}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn) { classifier("v2") })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.language.kotlin)

    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", libs.versions.fabric.api.get()))
    modImplementation(fabricApi.module("fabric-networking-api-v1", libs.versions.fabric.api.get()))

    // :OptiGUI transitive dependencies
    modLocalRuntime(fabricApi.module("fabric-events-interaction-v0", libs.versions.fabric.api.get()))
    modLocalRuntime(fabricApi.module("fabric-key-binding-api-v1", libs.versions.fabric.api.get()))
    modLocalRuntime(fabricApi.module("fabric-resource-loader-v0", libs.versions.fabric.api.get()))

    implementation(project(":OptiGUI", configuration = "namedElements"))
}

loom {
    runtimeOnlyLog4j = true

    runs {
        val client by getting
        val clientTest by registering {
            inherit(client)
            ideConfigGenerated(true)
        }
    }
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
        kotlinOptions {
            jvmTarget = javaVersion
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
}
