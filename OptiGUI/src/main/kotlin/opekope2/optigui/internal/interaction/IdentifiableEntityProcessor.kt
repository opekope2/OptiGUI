package opekope2.optigui.internal.interaction

import net.minecraft.entity.Entity
import opekope2.optigui.api.interaction.IEntityProcessor

internal class IdentifiableEntityProcessor<T : Entity>(
    val modId: String,
    val processor: IEntityProcessor<T>
) : IEntityProcessor<T> {
    override fun apply(obj: T): Any? = processor.apply(obj)
}
