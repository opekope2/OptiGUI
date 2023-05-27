import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import java.net.URL

plugins {
    id("fabric-loom")
    kotlin("jvm")
    id("net.kyori.blossom")
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
    }

    include(implementation("org.apache.commons", "commons-text", "1.10.0"))
    include(implementation("org.ini4j", "ini4j", "0.5.4"))

    testImplementation(kotlin("test"))

    if (project.hasProperty("javaSyntax")) {
        dokkaPlugin("org.jetbrains.dokka", "kotlin-as-java-plugin", "1.8.10")
    }
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
    test {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "SKIPPED", "FAILED")
        }
    }
}

evaluationDependsOn(":OptiGlue:1.18")
evaluationDependsOn(":OptiGlue:1.18.2")
evaluationDependsOn(":OptiGlue:1.19")
evaluationDependsOn(":OptiGlue:1.19.3")
evaluationDependsOn(":OptiGlue:1.19.4")

afterEvaluate {
    tasks.remapJar {
        nestedJars.from(project(":OptiGlue:1.18").outputJar)
        nestedJars.from(project(":OptiGlue:1.18.2").outputJar)
        nestedJars.from(project(":OptiGlue:1.19").outputJar)
        nestedJars.from(project(":OptiGlue:1.19.3").outputJar)
        nestedJars.from(project(":OptiGlue:1.19.4").outputJar)
    }
}

val Project.outputJar
    get() = tasks["remapJar"].outputs.files.firstOrNull()

tasks.dokkaHtml {
    moduleName.set("OptiGUI")
    moduleVersion.set(version as String)
    outputDirectory.set(
        buildDir.resolve(
            if (project.hasProperty("javaSyntax")) "dokka/javaHtml"
            else "dokka/kotlinHtml"
        )
    )

    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        footerMessage = "© 2022-2023 opekope2"
        customAssets = listOf(projectDir.resolve("logo-icon.svg"))
        separateInheritedMembers = true
    }

    dokkaSourceSets.configureEach {
        documentedVisibilities.set(
            setOf(
                DokkaConfiguration.Visibility.PUBLIC,
                DokkaConfiguration.Visibility.PROTECTED
            )
        )

        sourceLink {
            localDirectory.set(projectDir.resolve("src/main/kotlin"))
            remoteUrl.set(URL("https://github.com/opekope2/OptiGUI-Next/tree/$version/OptiGUI/src/main/kotlin"))
            remoteLineSuffix.set("#L")
        }

        externalDocumentationLink {
            val mappingsVersion = project.extra["yarn_mappings"]
            url.set(URL("https://maven.fabricmc.net/docs/yarn-$mappingsVersion/"))
            packageListUrl.set(URL("https://maven.fabricmc.net/docs/yarn-$mappingsVersion/element-list"))
        }
        externalDocumentationLink {
            val fabricVersion = project.extra["fabric_version"]
            url.set(URL("https://maven.fabricmc.net/docs/fabric-api-$fabricVersion/"))
            packageListUrl.set(URL("https://maven.fabricmc.net/docs/fabric-api-$fabricVersion/element-list"))
        }
        externalDocumentationLink {
            url.set(URL("https://ini4j.sourceforge.net/apidocs/"))
            packageListUrl.set(URL("https://ini4j.sourceforge.net/apidocs/package-list"))
        }

        perPackageOption {
            matchingRegex.set("""opekope2\.optigui\.internal(.*)""")
            suppress.set(true)
            documentedVisibilities.set(setOf())
        }
        perPackageOption {
            matchingRegex.set("""opekope2\.optigui\.mixin""")
            suppress.set(true)
            documentedVisibilities.set(setOf())
        }

        // Apply these last, otherwise the other options get ignored
        // You don't want to know how many hours I spent on this...
        jdkVersion.set(project.extra["java_version"] as Int)
        languageVersion.set(System.getProperty("kotlin_version"))
    }
}
