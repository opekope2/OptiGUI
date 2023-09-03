package opekope2.optigui.internal.interaction

import net.minecraft.entity.Entity
import opekope2.optigui.api.interaction.IEntityProcessor
import opekope2.util.IIdentifiable


internal class IdentifiableEntityProcessor<T : Entity>(
    override val id: String,
    val processor: IEntityProcessor<T>
) : IEntityProcessor<T>, IIdentifiable {
    override fun apply(obj: T): Any? = processor.apply(obj)
}
