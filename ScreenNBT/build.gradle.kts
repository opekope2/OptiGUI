plugins {
    alias(libs.plugins.fabric.loom)
}

base {
    archivesName = "optigui-screen-nbt"
}

version = libs.versions.optigui.get()
group = "opekope2.optigui"

repositories {
    maven("https://maven.terraformersmc.com/") {
        name = "Terraformers"
    }
    maven("https://maven.shedaniel.me/") {
        name = "Shedaniel"
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn) { classifier("v2") })
    modImplementation(libs.fabric.loader)
    implementation(project(":ScreenAPI", configuration = "namedElements"))
}

loom {
    runtimeOnlyLog4j = true
}

tasks {
    val javaVersion = libs.versions.java.get()

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        options.release = javaVersion.toInt()
    }

    jar {
        from(rootDir.resolve("COPYING"))
        from(rootDir.resolve("COPYING.LESSER"))
    }

    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "version" to version as String,
                    "minecraft" to libs.versions.minecraft.get(),
                    "java" to javaVersion,
                )
            )
        }
        filesMatching("*.mixins.json") {
            expand(
                mapOf(
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

    test {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "SKIPPED", "FAILED")
        }
    }
}
