package opekope2.optigui.registry

import net.minecraft.block.entity.BlockEntity
import opekope2.optigui.interaction.IBlockEntityProcessor

/**
 * Object managing block entity processors.
 */
object BlockEntityProcessorRegistry {
    private val blockEntityProcessors = mutableMapOf<Class<out BlockEntity>, IBlockEntityProcessor<out BlockEntity>>()

    /**
     * Sets the processor of a block entity if not set already.
     *
     * @param T The block entity type the processor processes
     * @param type The block entity type the processor processes
     * @param processor The block entity processor instance
     * @return `false`, if the given block entity already has a processor, `true` otherwise
     */
    fun <T : BlockEntity> trySet(type: Class<T>, processor: IBlockEntityProcessor<T>): Boolean {
        if (type in this) return false

        blockEntityProcessors[type] = processor
        return true
    }

    /**
     * Sets the processor of a block entity if not set already, or throws an exception.
     *
     * @param T The block entity type the processor processes
     * @param type The block entity type the processor processes
     * @param processor The block entity processor instance
     * @see trySet
     * @see contains
     */
    operator fun <T : BlockEntity> set(type: Class<T>, processor: IBlockEntityProcessor<T>) {
        if (!trySet(type, processor)) {
            throw IllegalStateException("A processor for $type has already been registered")
        }
    }

    /**
     * Checks if the given block entity has a processor.
     *
     * @param type The block entity class to query
     */
    operator fun <T : BlockEntity> contains(type: Class<T>): Boolean = type in blockEntityProcessors

    /**
     * Gets the registered processor of a block entity, if exists.
     *
     * @param type The block entity class to query
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <T : BlockEntity> get(type: Class<T>): IBlockEntityProcessor<T>? =
        blockEntityProcessors[type] as IBlockEntityProcessor<T>?


    /**
     * Runs the processor of [blockEntity] if registered, and returns its result.
     *
     * @param blockEntity The block entity to process
     */
    @JvmStatic
    fun processBlockEntity(blockEntity: BlockEntity): Any? = this[blockEntity.javaClass]?.process(blockEntity)
}
