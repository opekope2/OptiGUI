package opekope2.optigui.internal.interaction

import net.minecraft.block.entity.BlockEntity
import opekope2.optigui.interaction.BlockEntityPreprocessor

internal class IdentifiableBlockEntityPreprocessor(
    val modId: String,
    private val preprocessor: BlockEntityPreprocessor
) : BlockEntityPreprocessor {
    override fun process(blockEntity: BlockEntity) = preprocessor.process(blockEntity)
}
