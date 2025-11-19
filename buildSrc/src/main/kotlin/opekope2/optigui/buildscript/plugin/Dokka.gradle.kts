package opekope2.optigui.buildscript.plugin

import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import java.net.URLEncoder
import java.time.Year

plugins {
    `maven-publish`
    id("org.jetbrains.dokka")
}

val libs = the<LibrariesForLibs>() // https://stackoverflow.com/a/77181831

dokka {
    pluginsConfiguration.html {
        footerMessage = "© 2022-${Year.now().value} opekope2"
        customAssets.from(rootDir.resolve("assets/logo-icon.svg"))
        separateInheritedMembers = true
    }

    dokkaSourceSets.configureEach {
        documentedVisibilities(VisibilityModifier.Public, VisibilityModifier.Protected)

        val tag = URLEncoder.encode(version.toString(), Charsets.UTF_8)
        val baseUrl = "https://github.com/opekope2/OptiGUI/tree/$tag/${project.name}"
        sourceLink {
            localDirectory = projectDir.resolve("src/main/java")
            remoteUrl("$baseUrl/src/main/java")
            remoteLineSuffix = "#L"
        }
        sourceLink {
            localDirectory = projectDir.resolve("src/main/kotlin")
            remoteUrl("$baseUrl/src/main/kotlin")
            remoteLineSuffix = "#L"
        }

        // Apply these last, otherwise the other options get ignored
        // You don't want to know how many hours I spent on this...
        jdkVersion = libs.versions.java.map(String::toInt)
        languageVersion = libs.versions.kotlin
    }

    afterEvaluate {
        moduleVersion = version.toString()
    }
}

tasks {
    val javadocJar by registering(Jar::class) {
        dependsOn(dokkaGeneratePublicationHtml)
        from(dokkaGeneratePublicationHtml)
        archiveClassifier = "javadoc"
    }

    assemble { dependsOn(javadocJar) }
}

afterEvaluate {
    publishing.publications {
        named<MavenPublication>("maven") { artifact(tasks.named("javadocJar")) }
    }
}
