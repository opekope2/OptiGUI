package opekope2.optigui.buildscript.task

import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateI18nEnum : AbstractCodegenTask() {
    init {
        enumName.convention("I18n")
    }

    @get:Input
    abstract val enumPackage: Property<String>

    @get:Input
    abstract val enumName: Property<String>

    @TaskAction
    fun run() {
        deleteOutputDir()

        val enumPackage = enumPackage.get()
        val enumName = enumName.get()

        val packageDir = outputDir.dir(enumPackage.replace('.', '/')).get()
        packageDir.asFile.mkdirs()

        val json = JsonSlurper().setType(JsonParserType.LAX).parse(inputs.files.singleFile) as Map<String, String>
        val members = json.entries.sortedBy { it.key }.joinToString(separator = ",\n") { (key) ->
            val k = key.uppercase().replace("""[^a-zA-Z0-9]""".toRegex(), "_")
            """    $k("$key")"""
        }.trimStart()

        // language=kotlin
        val content = """
            |package $enumPackage
            |
            |import net.minecraft.network.chat.Component
            |import java.util.function.Supplier
            |
            |internal enum class $enumName(val key: String) {
            |    $members;
            |
            |    fun getText(vararg args: Any?) = Component.translatable(key, *args)
            |
            |    fun getTranslation(vararg args: Any?) = getText(*args).getString()
            |
            |    fun supplyTranslation(vararg args: Any?) = Supplier { getTranslation(*args) }
            |}
        """.trimMargin()
        packageDir.file("$enumName.kt").asFile.writeText(content)
    }
}
