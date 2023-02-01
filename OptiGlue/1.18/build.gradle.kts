plugins {
    id("fabric-loom")
    kotlin("jvm")
    id("net.kyori.blossom")
}

base { archivesName.set(project.extra["archives_base_name"] as String) }

version = "${project.extra["mod_version"]}-mc.${project.extra["minecraft_version"]}"
group = project.extra["maven_group"] as String

repositories {}

dependencies {
    minecraft("com.mojang", "minecraft", project.extra["minecraft_version"] as String)
    mappings(loom.layered {
        val mappingsDir = rootProject.projectDir.child("mappings").child(project.extra["minecraft_version"] as String)

        mappings(mappingsDir.child("mappings.tiny"))
        mappingsDir.listFiles { _, name -> name.endsWith(".mapping") }.forEach { mappings(it) { enigmaMappings() } }
    })
    modImplementation(fabricApi.module("fabric-resource-loader-v0", project.extra["fabric_version"] as String))
    modImplementation(
        "net.fabricmc", "fabric-language-kotlin", project.extra["fabric_language_kotlin_version"] as String
    )

    implementation(project(":OptiGUI", configuration = "namedElements"))
}

loom {
    clientOnlyMinecraftJar()
}

blossom.replaceToken("@mod_version@", version)

tasks {
    val javaVersion = JavaVersion.toVersion((project.extra["java_version"] as String).toInt())
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions { jvmTarget = javaVersion.toString() } }
    jar { from("LICENSE") { rename { "${it}_${base.archivesName.get()}" } } }
    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to version,
                    "fabric_language_kotlin" to project.extra["fabric_language_kotlin_version"] as String,
                    "java" to project.extra["java_version"] as String
                )
            )
        }
        filesMatching("*.mixins.json") { expand(mutableMapOf("java" to project.extra["java_version"] as String)) }
    }
    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}

fun File.child(name: String) = File(this, name)
