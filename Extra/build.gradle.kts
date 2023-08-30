plugins {
    id("fabric-loom")
    kotlin("jvm")
}

evaluationDependsOn(":OptiGUI")
evaluationDependsOn(":Api")
evaluationDependsOn(":Properties")

base { archivesName.set(project.extra["archives_base_name"] as String) }

version = project.extra["mod_version"] as String
group = project.extra["maven_group"] as String

repositories {
    mavenLocal()
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven") { name = "Modrinth" }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
    maven("https://cursemaven.com") {
        name = "CurseForge"
        content {
            includeGroup("curse.maven")
        }
    }
}

val extractNestedJars by configurations.creating
val nestedJarsDir = buildDir.resolve("nestedJars")

dependencies {
    minecraft("com.mojang", "minecraft", project.extra["minecraft_version"] as String)
    mappings("net.fabricmc", "yarn", project.extra["yarn_mappings"] as String, classifier = "v2")
    modImplementation("net.fabricmc", "fabric-loader", project.extra["loader_version"] as String)
    modImplementation(
        "net.fabricmc", "fabric-language-kotlin", project.extra["fabric_language_kotlin_version"] as String
    )

    modImplementation(fabricApi.module("fabric-events-interaction-v0", project.extra["fabric_version"] as String))
    modLocalRuntime("net.fabricmc.fabric-api", "fabric-api", project.extra["fabric_version"] as String)

    modImplementation(files(rootDir.resolve("lib/lilac-api-1.0.0-alpha.1-dev.jar")))
    modLocalRuntime(files(rootDir.resolve("lib/lilac-1.0.0-alpha.1-dev.jar")))

    localRuntime(project(":OptiGUI", configuration = "namedElements"))
    implementation(project(":Api", configuration = "namedElements"))
    implementation(project(":Properties", configuration = "namedElements"))

    extractNestedJars(modImplementation("maven.modrinth", "quickshulker", "1.4.0-1.20"))
    modImplementation("curse.maven", "more-chest-variants-lieonlion-858032", "4723273")
    modImplementation("curse.maven", "variant-barrels-fabric-576766", "4623658")

    // Gradle has skill issue and doesn't pull transitive deps.
    // But it pulls a newer version of DFU through mavenLocal, which crashes Minecraft.
    // Just as the founding fathers intended.
    localRuntime("org.apache.commons", "commons-text", "1.10.0")
    localRuntime("org.ini4j", "ini4j", "0.5.4")

    modLocalRuntime(fileTree(nestedJarsDir))
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
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to version,
                    "fabricloader" to project.extra["loader_version"] as String,
                    "fabric_language_kotlin" to project.extra["fabric_language_kotlin_version"] as String,
                    "java" to project.extra["java_version"] as String
                )
            )
        }
        filesMatching("*.mixins.json") {
            expand(mutableMapOf("java" to project.extra["java_version"] as String))
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
}

val prepareClientRun by tasks.creating {
    nestedJarsDir.mkdirs()
    outputs.dir(nestedJarsDir)

    doLast {
        val jars = extractNestedJars.files

        jars.forEach { jar ->
            zipTree(jar).visit {
                if (path.startsWith("META-INF/jars/")) {
                    copyTo(nestedJarsDir.resolve(name))
                }
            }
        }
    }
}

tasks["generateRemapClasspath"].dependsOn(prepareClientRun)
