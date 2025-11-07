plugins {
    alias(libs.plugins.fabric.loom)
}

base {
    archivesName = "optigui-screen-api"
}

version = libs.versions.optigui.get()
group = "opekope2.optigui"

repositories {
}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn) { classifier("v2") })
    api(libs.jsr305)
    modImplementation(libs.fabric.loader)
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
        from("LICENSE")
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
