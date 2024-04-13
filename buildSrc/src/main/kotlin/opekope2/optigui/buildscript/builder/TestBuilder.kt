package opekope2.optigui.buildscript.builder

import opekope2.optigui.buildscript.util.IDestination
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.property
import org.ini4j.Options

class TestBuilder(private val project: Project) : IDestination<String> {
    @Input
    override val destination: Property<String> = project.objects.property()

    @Optional
    @Nested
    val block: Property<TestPropertyBuilder> = project.objects.property()

    @Optional
    @Nested
    val entity: Property<TestPropertyBuilder> = project.objects.property()

    // TODO item

    fun block(id: String, action: TestPropertyBuilder.() -> Unit) {
        block = TestPropertyBuilder(id, project).apply(action)
    }

    fun entity(id: String, action: TestPropertyBuilder.() -> Unit) {
        entity = TestPropertyBuilder(id, project).apply(action)
    }

    internal fun build(replacementId: String) = Options().apply {
        val testProperties = block.orNull ?: entity.get()
        this[if (block.isPresent) "block" else "entity"] = testProperties.id.get()
        this["pos"] = testProperties.pos.get().toString()
        testProperties.nbt.orNull?.let { this["nbt"] = it }
        this["expected_texture"] = replacementId
    }
}
