plugins {
    id("fabric-loom")
    kotlin("jvm")
}

evaluationDependsOn(":Api")
evaluationDependsOn(":Filters")
evaluationDependsOn(":Properties")

base { archivesName.set(project.extra["archives_base_name"] as String) }

version = project.extra["mod_version"] as String
group = project.extra["maven_group"] as String

repositories {}

dependencies {
    minecraft("com.mojang", "minecraft", project.extra["minecraft_version"] as String)
    mappings("net.fabricmc", "yarn", project.extra["yarn_mappings"] as String, classifier = "v2")
    modImplementation("net.fabricmc", "fabric-loader", project.extra["loader_version"] as String)
    modImplementation(
        "net.fabricmc", "fabric-language-kotlin", project.extra["fabric_language_kotlin_version"] as String
    )

    modImplementation(fabricApi.module("fabric-networking-api-v1", project.extra["fabric_version"] as String))
    modImplementation(fabricApi.module("fabric-events-interaction-v0", project.extra["fabric_version"] as String))

    modImplementation(files(rootDir.resolve("lib/lilac-api-1.0.0-alpha.1-dev.jar")))
    modLocalRuntime(files(rootDir.resolve("lib/lilac-1.0.0-alpha.1-dev.jar")))

    implementation(project(":Api", configuration = "namedElements"))
    implementation(project(":Filters"))
    implementation(project(":Properties", configuration = "namedElements"))

    include(implementation("org.apache.commons", "commons-text", "1.10.0"))
    include(implementation("org.ini4j", "ini4j", "0.5.4"))

    testImplementation(kotlin("test"))
}

loom {
    accessWidenerPath.set(file("src/main/resources/optigui.accesswidener"))
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
        from(project(":Api").sourceSets["main"].output) {
            include("**/*.class")
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
        from(project(":Filters").sourceSets["main"].output) {
            include("**/*.class")
            include("**/*.kotlin_module")
        }
        from(project(":Properties").sourceSets["main"].output) {
            include("**/*.class")
            include("**/*.kotlin_module")
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

    test {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "SKIPPED", "FAILED")
        }
    }
}

if (project.hasProperty("distribution")) {
    val distribution: String by project.properties

    tasks {
        jar {
            manifest {
                attributes["Distribution"] = distribution
            }
        }

        remapJar {
            destinationDirectory.set(buildDir.resolve("libs/$distribution}"))
        }
    }
}
