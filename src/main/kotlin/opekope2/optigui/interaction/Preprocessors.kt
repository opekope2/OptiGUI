@file: JvmName("Preprocessors")

package opekope2.optigui.interaction

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity

/**
 * A block entity preprocessor, which extracts information from a block entity for further processing by filters.
 * This class provides [Interaction.data] (converts a block entity to an object,
 * where properties of the block entity are stored, which will be processed by filters supporting it).
 *
 * Each block entity can have one preprocessor registered with [registerPreprocessor].
 *
 * If a GUI screen is open, the preprocessor of the interacted block entity runs each tick,
 * so [process] should execute quickly.
 *
 * The result class of [process] should override the [Object.equals] method,
 * because filters will only be evaluated, if the preprocessor returns a different object,
 * because the block entity was changed (for example, moved to a different biome).
 */
fun interface IBlockEntityPreprocessor {
    /**
     * Processes a block entity.
     *
     * @param blockEntity The source block entity
     * @return An object, which will be included in [Interaction.data], and processed by [opekope2.filter.Filter.evaluate]
     */
    fun process(blockEntity: BlockEntity): Any?
}

/**
 * An entity preprocessor, which extracts information from an entity for further processing by filters.
 * This class provides [Interaction.data] (converts an entity to an object,
 * where properties of the entity are stored, which will be processed by filters supporting it).
 *
 * Each entity can have one preprocessor registered with [registerPreprocessor].
 *
 * If a GUI screen is open, the preprocessor of the interacted entity runs each tick,
 * so [process] should execute quickly.
 *
 * The result class of [process] should override the [Object.equals] method,
 * because filters will only be evaluated, if the preprocessor returns a different object,
 * because the entity was changed (for example, moved to a different biome).
 */
fun interface IEntityPreprocessor {
    /**
     * Creates an interaction data based on a block entity.
     *
     * @param entity The source entity
     * @return An object, which will be included in [Interaction.data], and processed by [opekope2.filter.Filter.evaluate]
     */
    fun process(entity: Entity): Any?
}

internal val blockEntityPreprocessors = mutableMapOf<Class<out BlockEntity>, IBlockEntityPreprocessor>()
internal val entityPreprocessors = mutableMapOf<Class<out Entity>, IEntityPreprocessor>()

/**
 * Registers the preprocessor for a block entity.
 *
 * @param type The block entity type the preprocessor processes
 * @param processor The block entity preprocessor instance
 * @return `true` if registration is successful, `false` if the given block entity already has a preprocessor registered
 */
fun registerPreprocessor(type: Class<out BlockEntity>, processor: IBlockEntityPreprocessor): Boolean {
    if (type in blockEntityPreprocessors) return false

    blockEntityPreprocessors[type] = processor
    return true
}

/**
 * Registers the preprocessor for a block entity.
 *
 * @param T The block entity type the preprocessor processes
 * @param processor The block entity preprocessor instance
 * @return `true` if registration is successful, `false` if the given block entity already has a preprocessor registered
 */
inline fun <reified T : BlockEntity> registerPreprocessor(processor: IBlockEntityPreprocessor) =
    registerPreprocessor(T::class.java, processor)

/**
 * Registers the preprocessor for an entity.
 *
 * @param type The entity type the preprocessor processes
 * @param processor The entity preprocessor instance
 * @return `true` if registration is successful, `false` if the given entity already has a preprocessor registered
 */
fun registerPreprocessor(type: Class<out Entity>, processor: IEntityPreprocessor): Boolean {
    if (type in entityPreprocessors) return false

    entityPreprocessors[type] = processor
    return true
}

/**
 * Registers the preprocessor for an entity.
 *
 * @param T The entity type the preprocessor processes
 * @param processor The entity preprocessor instance
 * @return `true` if registration is successful, `false` if the given entity already has a preprocessor registered
 */
inline fun <reified T : Entity> registerPreprocessor(processor: IEntityPreprocessor) =
    registerPreprocessor(T::class.java, processor)
