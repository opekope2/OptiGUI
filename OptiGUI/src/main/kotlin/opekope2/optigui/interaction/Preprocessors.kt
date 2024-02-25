package opekope2.optigui.interaction

import net.minecraft.block.entity.BlockEntity
import opekope2.optigui.internal.interaction.PreprocessorStore

/**
 * Interface to registered [BlockEntityPreprocessor] instances.
 */
object Preprocessors {
    /**
     * Runs the preprocessor of [blockEntity] if registered, and returns its result.
     *
     * @param blockEntity The block entity to process
     */
    @JvmStatic
    fun preprocessBlockEntity(blockEntity: BlockEntity): Any? =
        PreprocessorStore.blockEntityPreprocessors[blockEntity.javaClass]?.process(blockEntity)
}
