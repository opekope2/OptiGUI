package opekope2.optigui.internal.interaction

import net.minecraft.entity.Entity
import opekope2.optigui.interaction.EntityPreprocessor

internal class IdentifiableEntityPreprocessor(
    val modId: String,
    private val preprocessor: EntityPreprocessor
) : EntityPreprocessor {
    override fun process(entity: Entity) = preprocessor.process(entity)
}
