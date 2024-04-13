package opekope2.optigui.buildscript.builder

import opekope2.optigui.buildscript.data.Position
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.property

class TestPropertyBuilder(project: Project) {
    constructor(id: String, project: Project) : this(project) {
        this.id = id
    }

    @Input
    val id: Property<String> = project.objects.property()

    @Nested
    val pos: Property<Position> = project.objects.property()

    @Input
    @Optional
    val nbt: Property<String?> = project.objects.property()

    fun pos(x: Int, y: Int, z: Int) {
        pos = Position(x, y, z)
    }
}
