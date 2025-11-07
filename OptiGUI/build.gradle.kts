import groovy.json.JsonSlurper

plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    maven("https://maven.terraformersmc.com/") { name = "Terraformers" }
    maven("https://maven.shedaniel.me/") { name = "Shedaniel" }
}

dependencies {
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

tasks {
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
                
                import net.minecraft.text.MutableText
                import net.minecraft.text.Text
                import java.util.function.Supplier
                
                internal enum class I18n(private val key: String, private val fallback: String) {
                    %s;
                    
                    fun getText(vararg args: Any?): MutableText = Text.translatableWithFallback(key, fallback, *args)

                    fun getTranslation(vararg args: Any?): String = getText(*args).getString()
                    
                    fun supplyTranslation(vararg args: Any?): Supplier<String> = Supplier { getTranslation(*args) }
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
