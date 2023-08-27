package opekope2.optigui.internal.interaction

import net.minecraft.block.entity.BlockEntity
import opekope2.optigui.api.interaction.IBlockEntityProcessor

internal class IdentifiableBlockEntityProcessor<T : BlockEntity>(
    val modId: String,
    val processor: IBlockEntityProcessor<T>
) : IBlockEntityProcessor<T> {
    override fun apply(obj: T): Any? = processor.apply(obj)
}
