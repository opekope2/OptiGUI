package opekope2.optigui.buildscript.task

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateI18nEnum : DefaultTask() {
    init {
        outputDir.convention(project.layout.buildDirectory.dir("generated/$name"))
        enumName.convention("I18n")
    }

    @get:InputFile
    abstract val langFile: RegularFileProperty

    @get:Input
    abstract val enumName: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        outputDir.asFile.get().deleteRecursively()
        val packageDir = outputDir.dir(packageName.get().replace('.', '/')).get()
        packageDir.asFile.mkdirs()

        val json = JsonSlurper().parse(langFile.asFile.get()) as Map<String, String>
        val members = json.entries.joinToString(separator = ",\n") { (key, value) ->
            val k = key.uppercase().replace("""[^a-zA-Z0-9]""".toRegex(), "_")
            """    $k("$key", "$value")"""
        }.trimStart()

        val content = """
            |package ${packageName.get()}
            |
            |import net.minecraft.text.MutableText
            |import net.minecraft.text.Text
            |import java.util.function.Supplier
            |
            |internal enum class ${enumName.get()}(private val key: String, private val fallback: String) {
            |    $members;
            |    
            |    fun getText(vararg args: Any?): MutableText = Text.translatableWithFallback(key, fallback, *args)
            |
            |    fun getTranslation(vararg args: Any?): String = getText(*args).getString()
            |    
            |    fun supplyTranslation(vararg args: Any?): Supplier<String> = Supplier { getTranslation(*args) }
            |}
        """.trimMargin()
        packageDir.file("${enumName.get()}.kt").asFile.writeText(content)
    }
}
