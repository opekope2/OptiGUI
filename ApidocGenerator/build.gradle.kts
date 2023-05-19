import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory


plugins {
    base
}

repositories {
    mavenCentral()
}

val dokkaVersion = "1.8.10"
val dokkaHtmlPluginVersion = "0.8.1"
val freemarkerVersion = "2.3.32"

val kotlinVersion = System.getProperty("kotlin_version")
val modVersion = project.extra["mod_version"] as String
val ini4jVersion = project(":OptiGUI").extra["ini4j_version"] as String
val minecraftVersion = project(":OptiGUI").extra["minecraft_version"] as String
val fabricApiVersion = project(":OptiGUI").extra["fabric_version"] as String
val yarnMappingsVersion = project(":OptiGUI").extra["yarn_mappings"] as String

val download by configurations.creating

dependencies {
    download("org.jetbrains.dokka", "dokka-cli", dokkaVersion)
    download("org.jetbrains.dokka", "dokka-base", dokkaVersion)
    download("org.jetbrains.dokka", "dokka-analysis", dokkaVersion)
    download("org.jetbrains.dokka", "kotlin-analysis-compiler", dokkaVersion)
    download("org.jetbrains.dokka", "kotlin-analysis-intellij", dokkaVersion)
    download("org.jetbrains.kotlinx", "kotlinx-html-jvm", dokkaHtmlPluginVersion)
    download("org.freemarker", "freemarker", freemarkerVersion)
    download("org.jetbrains.dokka", "kotlin-as-java-plugin", dokkaVersion)
    download("org.jetbrains.kotlin", "kotlin-stdlib", kotlinVersion)
    download("org.ini4j", "ini4j", ini4jVersion)
}

val downloadDokka by tasks.registering(Copy::class) {
    from(download)
    into("bin/dokka")
}

val generateKotlinDokkaConfig by tasks.registering {
    doLast {
        file("dokka.g.json").writeText(createDokkaConfig(java = false))
    }
}

val generateJavaDokkaConfig by tasks.registering {
    doLast {
        file("dokka-java.g.json").writeText(createDokkaConfig(java = true))
    }
}

val generateKdoc by tasks.registering(Exec::class) {
    dependsOn(downloadDokka, generateKotlinDokkaConfig)
    workingDir(projectDir)
    commandLine("java", "-jar", "bin/dokka/dokka-cli-$dokkaVersion.jar", "dokka.g.json")
}

val generateJavadoc by tasks.registering(Exec::class) {
    dependsOn(downloadDokka, generateJavaDokkaConfig)
    workingDir(projectDir)
    commandLine("java", "-jar", "bin/dokka/dokka-cli-$dokkaVersion.jar", "dokka-java.g.json")
}

fun createDokkaConfig(java: Boolean): String {
    val outputDir =
        if (java) "../docs/javadoc/$modVersion"
        else "../docs/kdoc/$modVersion"
    val javaPlugin = run {
        val pluginPath = "bin/dokka/kotlin-as-java-plugin-$dokkaVersion.jar"

        if (java) """"$pluginPath","""
        else ""
    }
    val fabricEventsInteractionVersion = getFabricModuleVersion("fabric-events-interaction-v0")
    val minecraftUnderscore = minecraftVersion.replace('.', '_')
    val yarnMappingsUnderscore = yarnMappingsVersion.replace('.', '_').replace('+', '_')

    return """
        {
            "moduleName": "OptiGUI",
            "moduleVersion": "$modVersion",
            "outputDir": "$outputDir",
            "sourceSets": [
                {
                    "displayName": "jvm",
                    "sourceSetID": {
                        "scopeId": "OptiGUI",
                        "sourceSetName": "main"
                    },
                    "sourceRoots": [
                        "$rootDir/OptiGUI/src/main/kotlin/",
                        "$rootDir/OptiGUI/src/main/java/"
                    ],
                    "classpath": [
                        "${project.gradle.gradleUserHomeDir}/caches/fabric-loom/$minecraftVersion/net.fabricmc.yarn.$minecraftUnderscore.$yarnMappingsVersion-v2/minecraft-merged-named.jar",
                        "$rootDir/.gradle/loom-cache/remapped_mods/net_fabricmc_yarn_${minecraftUnderscore}_${yarnMappingsUnderscore}_v2/net/fabricmc/fabric-api/fabric-events-interaction-v0/$fabricEventsInteractionVersion/fabric-events-interaction-v0-$fabricEventsInteractionVersion.jar",
                        "$projectDir/bin/dokka/kotlin-stdlib-$kotlinVersion.jar",
                        "$projectDir/bin/dokka/ini4j-$ini4jVersion.jar"
                    ]
                }
            ],
            "sourceLinks": [
                {
                  "localDirectory": "$rootDir/OptiGUI/src/main/kotlin/",
                  "remoteUrl": "https://github.com/opekope2/OptiGUI-Next/tree/$modVersion/OptiGUI/src/main/kotlin",
                  "remoteLineSuffix": "#L"
                }
            ],
            "externalDocumentationLinks": [
                {
                    "url": "https://maven.fabricmc.net/docs/yarn-$yarnMappingsVersion/",
                    "packageListUrl": "https://maven.fabricmc.net/docs/yarn-$yarnMappingsVersion/element-list"
                },
                {
                    "url": "https://maven.fabricmc.net/docs/fabric-api-$fabricApiVersion/",
                    "packageListUrl": "https://maven.fabricmc.net/docs/fabric-api-$fabricApiVersion/element-list"
                },
                {
                    "url": "https://ini4j.sourceforge.net/apidocs/",
                    "packageListUrl": "https://ini4j.sourceforge.net/apidocs/package-list"
                },
                {
                    "url": "https://docs.oracle.com/javase/8/docs/api/",
                    "packageListUrl": "https://docs.oracle.com/javase/8/docs/api/package-list"
                },
                {
                    "url": "https://kotlinlang.org/api/latest/jvm/stdlib/",
                    "packageListUrl": "https://kotlinlang.org/api/latest/jvm/stdlib/package-list"
                }
            ],
            "perPackageOptions": [
                {
                    "matchingRegex": "opekope2\\.optigui\\.internal(.*)",
                    "suppress": true,
                    "documentedVisibilities": []
                },
                {
                    "matchingRegex": "opekope2\\.optigui\\.mixin",
                    "suppress": true,
                    "documentedVisibilities": []
                }
            ],
            "pluginsClasspath": [
                "bin/dokka/dokka-base-$dokkaVersion.jar",
                "bin/dokka/kotlinx-html-jvm-$dokkaHtmlPluginVersion.jar",
                $javaPlugin
                "bin/dokka/dokka-analysis-$dokkaVersion.jar",
                "bin/dokka/kotlin-analysis-intellij-$dokkaVersion.jar",
                "bin/dokka/kotlin-analysis-compiler-$dokkaVersion.jar",
                "bin/dokka/freemarker-$freemarkerVersion.jar"
            ],
            "pluginsConfiguration": [
                {
                    "fqPluginName": "org.jetbrains.dokka.base.DokkaBase",
                    "serializationFormat": "JSON",
                    "values": "{\"footerMessage\":\"© 2022-2023 opekope2\",\"customAssets\":[\"logo-icon.svg\"]}"
                }
            ]
        }
    """.trimIndent()
}

// Adapted from https://github.com/FabricMC/fabric-loom/blob/1.0/src/main/java/net/fabricmc/loom/configuration/FabricApiExtension.java
fun getFabricModuleVersion(module: String): String {
    val pom = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        .parse("https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/$fabricApiVersion/fabric-api-$fabricApiVersion.pom")

    val deps = (pom.getElementsByTagName("dependencies").item(0) as Element).getElementsByTagName("dependency")

    for (i in 0 until deps.length) {
        val dep = deps.item(i) as Element
        val artifact = dep.getElementsByTagName("artifactId").item(0) as Element
        val version = dep.getElementsByTagName("version").item(0) as Element

        if (artifact.textContent == module) return version.textContent
    }

    throw Exception("Module '$module' not found.")
}
