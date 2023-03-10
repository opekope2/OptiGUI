import org.jetbrains.dokka.gradle.DokkaTask
import java.time.LocalDateTime
import java.net.URL as JavaNetUrl

plugins {
    id("fabric-loom")
    kotlin("jvm")
    id("net.kyori.blossom")
    id("maven-publish")
    id("org.jetbrains.dokka")
}

base { archivesName.set(project.extra["archives_base_name"] as String) }

version = project.extra["mod_version"] as String
group = project.extra["maven_group"] as String

repositories {}

dependencies {
    minecraft("com.mojang", "minecraft", project.extra["minecraft_version"] as String)
    mappings("net.fabricmc", "yarn", project.extra["yarn_mappings"] as String, null, "v2")
    modImplementation("net.fabricmc", "fabric-loader", project.extra["loader_version"] as String)
    modImplementation(
        "net.fabricmc",
        "fabric-language-kotlin",
        project.extra["fabric_language_kotlin_version"] as String
    )

    (project.extra["fabric_version"] as String).also { fabricVersion ->
        modImplementation(fabricApi.module("fabric-lifecycle-events-v1", fabricVersion))
        modImplementation(fabricApi.module("fabric-networking-api-v1", fabricVersion))
        modImplementation(fabricApi.module("fabric-events-interaction-v0", fabricVersion))

        // OptiGlue transitive dependency.
        // Declare explicitly, otherwise fabric loader will not find it.
        modRuntimeOnly(fabricApi.module("fabric-resource-loader-v0", fabricVersion))
    }

    runtimeOnly(project(":OptiGlue:1.18", configuration = "namedElements"))
    runtimeOnly(project(":OptiGlue:1.18.2", configuration = "namedElements"))
    runtimeOnly(project(":OptiGlue:1.19", configuration = "namedElements"))
    runtimeOnly(project(":OptiGlue:1.19.3", configuration = "namedElements"))

    include(project(":OptiGlue:1.18", configuration = "namedElements"))
    include(project(":OptiGlue:1.18.2", configuration = "namedElements"))
    include(project(":OptiGlue:1.19", configuration = "namedElements"))
    include(project(":OptiGlue:1.19.3", configuration = "namedElements"))

    include(implementation("org.apache.commons", "commons-text", "1.10.0"))

    testImplementation(kotlin("test"))
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
                    "fabricloader" to project.extra["loader_version"] as String,
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

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("PASSED", "SKIPPED", "FAILED")
    }
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets.configureEach {
        val dokkaBaseConfiguration = """
            {
              "footerMessage": "&copy; 2022-${LocalDateTime.now().year} opekope2"
            }
        """
        pluginsMapConfiguration.set(
            mapOf(
                // fully qualified plugin name to json configuration
                "org.jetbrains.dokka.base.DokkaBase" to dokkaBaseConfiguration
            )
        )

        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            remoteUrl.set(JavaNetUrl("https://github.com/opekope2/OptiGUI-Next/tree/main/OptiGUI/src"))
            remoteLineSuffix.set("#L")
        }
        perPackageOption {
            matchingRegex.set("opekope2\\.optigui\\.internal(.*)")
            suppress.set(true)
        }
    }
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = base.archivesName.get()
            version = project.version.toString()

            artifact(javadocJar.get())

            from(components["java"])
        }
    }
}
