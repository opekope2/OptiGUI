package opekope2.optigui.buildscript.builder

import com.github.holgerbrandl.jsonbuilder.json
import opekope2.optigui.buildscript.data.Position
import opekope2.optigui.buildscript.util.IDestination
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.property
import org.json.JSONObject

class TestBuilder(private val project: Project) : IDestination<String> {
    @Input
    override val destination: Property<String> = project.objects.property()

    @Optional
    @Nested
    val block: Property<TestBlockPropertyBuilder> = project.objects.property()

    @Optional
    @Nested
    val entity: Property<TestPropertyBuilder> = project.objects.property()

    // TODO item

    fun block(id: String, action: TestBlockPropertyBuilder.() -> Unit) {
        block = TestBlockPropertyBuilder(id, project).apply(action)
    }

    fun entity(id: String, action: TestPropertyBuilder.() -> Unit) {
        entity = TestPropertyBuilder(id, project).apply(action)
    }

    internal fun build(replacementId: String): JSONObject {
        val isBlock = block.isPresent
        val pos: Position
        val nbt: String?
        if (isBlock) {
            pos = block.get().pos.get()
            nbt = block.get().nbt.orNull
        } else {
            pos = entity.get().pos.get()
            nbt = entity.get().nbt.orNull
        }

        return json {
            if (isBlock) {
                "blockState" to {
                    "Name" to block.get().id.get()
                    "Properties" to {
                        block.get().stateProperties.get().forEach { (key, value) ->
                            key to value
                        }
                    }
                }
            } else {
                "entityId" to entity.get().id.get()
            }
            "pos" to pos.toArray()
            if (nbt != null) {
                "nbt" to nbt
            }
            "expectedTexture" to replacementId
        }
    }
}
