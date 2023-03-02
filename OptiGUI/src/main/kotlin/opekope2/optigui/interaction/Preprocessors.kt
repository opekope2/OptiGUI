package opekope2.optigui.interaction

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import opekope2.optigui.internal.interaction.PreprocessorStore

/**
 * Interface to registered [BlockEntityPreprocessor] and [EntityPreprocessor] instances.
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

    /**
     * Runs the preprocessor of [entity] if registered, and returns its result.
     *
     * @param entity The entity to process
     */
    @JvmStatic
    fun preprocessEntity(entity: Entity): Any? =
        PreprocessorStore.entityPreprocessors[entity.javaClass]?.process(entity)
}
