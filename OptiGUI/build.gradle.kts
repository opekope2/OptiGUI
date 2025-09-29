import groovy.json.JsonSlurper
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
}

base {
    archivesName = "optigui"
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
    modImplementation(libs.fabric.language.kotlin)
    modImplementation(libs.fabric.api)
    localRuntime(project(":ScreenNBT", configuration = "namedElements"))

    api(project(":ScreenAPI", configuration = "namedElements"))
    include(project(":ScreenAPI"))

    modImplementation(libs.cloth.config.fabric) {
        exclude(group = "net.fabricmc.fabric-api")
    }
    modImplementation(libs.modmenu)

    implementation(libs.ini4j)
    include(libs.ini4j)

    testImplementation(kotlin("test"))
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

    withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(javaVersion)
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
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
                    "fabric_loader" to libs.versions.fabric.loader.get(),
                    "fabric_api" to libs.versions.fabric.api.get(),
                    "fabric_language_kotlin" to libs.versions.fabric.language.kotlin.get(),
                    "minecraft" to libs.versions.minecraft.get(),
                    "java" to javaVersion,
                    "cloth_config" to libs.versions.cloth.config.fabric.get(),
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

    val generateI18n by registering {
        val jsonPath = "src/main/resources/assets/optigui/lang/en_us.json"
        val outFile = project.layout.buildDirectory.file("generated/src/main/kotlin/opekope2/optigui/internal/I18n.kt")

        inputs.file(jsonPath)
        outputs.file(outFile)

        doLast {
            val json = JsonSlurper().parse(file(jsonPath)) as Map<String, String>
            val members = json.entries.joinToString(separator = ",\n    ") { (key, value) ->
                val k = key.uppercase().replace("""[^a-zA-Z0-9]""".toRegex(), "_")
                """$k("$key", "$value")"""
            }
            val enum = """
                package opekope2.optigui.internal
                
                import com.google.common.base.Suppliers
                import net.minecraft.text.MutableText
                import net.minecraft.text.Text
                import java.util.function.Supplier
                
                internal enum class I18n(private val key: String, private val fallback: String) {
                    %s;
                    
                    fun getText(vararg args: Any?): MutableText = Text.translatableWithFallback(key, fallback, *args)

                    fun getTranslation(vararg args: Any?): String = getText(*args).getString()
                    
                    fun supplyTranslation(vararg args: Any?): Supplier<String> = Suppliers.memoize { getTranslation(*args) }
                }
            """.trimIndent().format(members)

            file(outFile).writeText(enum)
        }
    }

    val compileKotlin by getting { dependsOn(generateI18n) }
    val sourcesJar by getting { dependsOn(generateI18n) }
}

sourceSets {
    main {
        kotlin {
            srcDir(project.layout.buildDirectory.dir("generated/src/main/kotlin"))
        }
    }
}
