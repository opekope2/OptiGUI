package opekope2.optigui.buildscript.builder

import opekope2.optigui.buildscript.util.IDestination
import opekope2.optigui.buildscript.util.ISource
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.property

class ResourcePackBuilder(private val project: Project) : ISource<String>, IDestination<String> {
    @Input
    override val source: Property<String> = project.objects.property()

    @Input
    override val destination: Property<String> = project.objects.property()

    @Optional
    @Nested
    val properties: Property<PropertiesBuilder> = project.objects.property()

    @Optional
    @Nested
    val ini: Property<IniBuilder> = project.objects.property()

    @Nested
    val test = TestBuilder(project)

    fun properties(action: PropertiesBuilder.() -> Unit) {
        properties = PropertiesBuilder(project).apply(action)
    }

    fun ini(action: IniBuilder.() -> Unit) {
        ini = IniBuilder(project).apply(action)
    }

    fun test(action: TestBuilder.() -> Unit) {
        test.action()
    }
}
