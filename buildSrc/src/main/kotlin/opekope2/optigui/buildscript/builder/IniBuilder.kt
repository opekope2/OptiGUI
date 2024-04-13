package opekope2.optigui.buildscript.builder

import opekope2.optigui.buildscript.util.IDestination
import org.gradle.api.Project
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import org.ini4j.Ini

class IniBuilder(project: Project) : IDestination<String> {
    @Input
    override val destination: Property<String> = project.objects.property()

    @Input
    @Optional
    val selectors: MapProperty<String, String> =
        project.objects.mapProperty<String, String>().convention(mutableMapOf())

    fun selector(key: String, value: String) {
        selectors.put(key, value)
    }

    internal fun build(container: String, replacementId: String) = Ini().apply {
        val section = add(container)
        section["replacement"] = replacementId
        section.putAll(selectors.get())
    }
}

