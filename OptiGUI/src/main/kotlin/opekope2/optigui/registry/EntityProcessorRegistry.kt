package opekope2.optigui.registry

import net.minecraft.entity.Entity
import opekope2.optigui.interaction.IEntityProcessor

/**
 * Object managing entity processors.
 */
object EntityProcessorRegistry {
    private val entityProcessors = mutableMapOf<Class<out Entity>, IEntityProcessor<out Entity>>()

    /**
     * Sets the processor of an entity if not set already.
     *
     * @param T The entity type the processor processes
     * @param type The entity type the processor processes
     * @param processor The entity processor instance
     * @return `false`, if the given entity already has a processor, `true` otherwise
     */
    fun <T : Entity> trySet(type: Class<T>, processor: IEntityProcessor<T>): Boolean {
        if (type in this) return false

        entityProcessors[type] = processor
        return true
    }

    /**
     * Sets the processor of an entity if not set already, or throws an exception.
     *
     * @param T The entity type the processor processes
     * @param type The entity type the processor processes
     * @param processor The entity processor instance
     * @see trySet
     * @see contains
     */
    operator fun <T : Entity> set(type: Class<T>, processor: IEntityProcessor<T>) {
        if (!trySet(type, processor)) {
            throw IllegalStateException("A processor for $type has already been registered")
        }
    }

    /**
     * Checks if the given entity has a processor.
     *
     * @param type The entity class to query
     */
    operator fun <T : Entity> contains(type: Class<T>): Boolean = type in entityProcessors

    /**
     * Gets the registered processor of an entity, if exists.
     *
     * @param type The entity class to query
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <T : Entity> get(type: Class<T>): IEntityProcessor<T>? = entityProcessors[type] as IEntityProcessor<T>?


    /**
     * Runs the processor of [entity] if registered, and returns its result.
     *
     * @param entity The entity to process
     */
    @JvmStatic
    fun processEntity(entity: Entity): Any? = this[entity.javaClass]?.process(entity)
}
