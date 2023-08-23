import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import java.time.Year

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka", "dokka-base", project.extra["dokka_version"] as String)
    }
}

tasks {
    dokkaHtmlMultiModule {
        moduleName.set("OptiGUI")
        moduleVersion.set(project.extra["mod_version"] as String)
        outputDirectory.set(
            buildDir.resolve(
                if (project.hasProperty("javaSyntax")) "docs/javaHtml"
                else "docs/kotlinHtml"
            )
        )

        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
            footerMessage = "© 2022-${Year.now().value} opekope2"
            customAssets = listOf(rootDir.resolve("logo-icon.svg"))
            includes.from(rootDir.resolve("modules.md"))
        }
    }
}
