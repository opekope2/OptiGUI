package opekope2.optigui.buildscript.builder

import opekope2.optigui.buildscript.util.IDestination
import org.gradle.api.Project
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import org.ini4j.Options

class PropertiesBuilder(project: Project) : IDestination<String> {
    @Input
    override val destination: Property<String> = project.objects.property()

    @Input
    @Optional
    val properties: MapProperty<String, String> =
        project.objects.mapProperty<String, String>().convention(mutableMapOf())

    fun property(key: String, value: String) {
        properties.put(key, value)
    }

    internal fun build(replacementId: String) = Options().apply {
        this["texture"] = replacementId
        putAll(properties.get())
    }
}
