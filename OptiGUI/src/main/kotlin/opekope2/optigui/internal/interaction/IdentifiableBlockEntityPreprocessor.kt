package opekope2.optigui.internal.interaction

import net.minecraft.block.entity.BlockEntity
import opekope2.optigui.interaction.BlockEntityPreprocessor

internal class IdentifiableBlockEntityPreprocessor<T : BlockEntity>(
    val modId: String,
    private val preprocessor: BlockEntityPreprocessor<T>
) : BlockEntityPreprocessor<T> {
    override fun process(blockEntity: T) = preprocessor.process(blockEntity)
}
