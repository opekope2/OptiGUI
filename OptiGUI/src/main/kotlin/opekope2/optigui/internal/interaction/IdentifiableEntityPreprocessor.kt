package opekope2.optigui.internal.interaction

import net.minecraft.entity.Entity
import opekope2.optigui.interaction.EntityPreprocessor

internal class IdentifiableEntityPreprocessor<T : Entity>(
    val modId: String,
    private val preprocessor: EntityPreprocessor<T>
) : EntityPreprocessor<T> {
    override fun process(entity: T) = preprocessor.process(entity)
}
