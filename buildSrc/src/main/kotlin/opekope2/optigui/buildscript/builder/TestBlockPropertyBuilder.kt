package opekope2.optigui.buildscript.builder

import org.gradle.api.Project
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.mapProperty

class TestBlockPropertyBuilder(id: String, project: Project) : TestPropertyBuilder(id, project) {
    @Input
    @Optional
    val stateProperties: MapProperty<String, String> =
        project.objects.mapProperty<String, String>().convention(mutableMapOf())

    fun stateProperty(key: String, value: String) {
        stateProperties.get()[key] = value
    }
}
