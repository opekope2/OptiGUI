package opekope2.optigui.internal.interaction

import net.minecraft.block.entity.BlockEntity
import opekope2.optigui.api.interaction.IBlockEntityProcessor
import opekope2.optigui.api.interaction.IInteractionData
import opekope2.util.IIdentifiable


internal class IdentifiableBlockEntityProcessor<T : BlockEntity>(
    override val id: String,
    val processor: IBlockEntityProcessor<T>
) : IBlockEntityProcessor<T>, IIdentifiable {
    override fun apply(obj: T): IInteractionData? = processor.apply(obj)
}
