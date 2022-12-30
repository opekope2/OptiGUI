package opekope2.optigui.interaction

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity

/**
 * An interaction data factory, which extracts information from a block entity for further processing by filters.
 * This class provides [Interaction.data] (converts a block entity to an object,
 * where properties of the block entity are stored, which will be processed by filters supporting it).
 * Each block entity can have one interaction data provider registered with [registerInteractionDataFactory].
 * If a GUI screen is open, the interaction data provider of the interacted block entity runs each tick,
 * so [createInteractionData] should execute quickly.
 * The result class of [createInteractionData] should override the [Object.equals] method,
 * because filters will only be evaluated, if the interaction data provider returned a different object,
 * because the block entity was changed (for example, moved to a different biome).
 */
fun interface IBlockEntityInteractionDataFactory {
    /**
     * Creates an interaction data based on a block entity.
     *
     * @param blockEntity The source block entity
     * @return An object, which will be included in [Interaction.data], and processed by [opekope2.filter.Filter.test]
     */
    fun createInteractionData(blockEntity: BlockEntity): Any?
}

/**
 * An interaction data factory, which extracts information from an entity for further processing by filters.
 * This class provides [Interaction.data] (converts an entity to an object,
 * where properties of the entity are stored, which will be processed by filters supporting it).
 * Each entity can have one interaction data provider registered with [registerInteractionDataFactory].
 * If a GUI screen is open, the interaction data provider of the interacted entity runs each tick,
 * so [createInteractionData] should execute quickly.
 * The result class of [createInteractionData] should override the [Object.equals] method,
 * because filters will only be evaluated, if the interaction data provider returned a different object,
 * because the entity was changed (for example, moved to a different biome).
 */
fun interface IEntityInteractionDataFactory {
    /**
     * Creates an interaction data based on a block entity.
     *
     * @param entity The source entity
     * @return An object, which will be included in [Interaction.data], and processed by [opekope2.filter.Filter.test]
     */
    fun createInteractionData(entity: Entity): Any?
}

internal val blockEntityFactories = mutableMapOf<Class<out BlockEntity>, IBlockEntityInteractionDataFactory>()
internal val entityFactories = mutableMapOf<Class<out Entity>, IEntityInteractionDataFactory>()

/**
 * Registers an interaction data factory for a block entity.
 *
 * @param blockEntityType The block entity type the factory converts from
 * @param factory The block entity interaction data factory
 * @return `true` if registration is successful, `false` if the given block entity already has an interaction data provider
 */
fun registerInteractionDataFactory(
    blockEntityType: Class<out BlockEntity>, factory: IBlockEntityInteractionDataFactory
): Boolean {
    if (blockEntityType in blockEntityFactories) return false

    blockEntityFactories[blockEntityType] = factory
    return true
}

/**
 * Registers an interaction data factory for a block entity.
 *
 * @param T The block entity type the factory converts from
 * @param factory The block entity interaction data factory
 * @return `true` if registration is successful, `false` if the given block entity already has an interaction data provider
 */
inline fun <reified T : BlockEntity> registerInteractionDataFactory(factory: IBlockEntityInteractionDataFactory) =
    registerInteractionDataFactory(T::class.java, factory)

/**
 * Registers an interaction data factory for an entity.
 *
 * @param entityType The entity type the factory converts from
 * @param factory The entity interaction data factory
 * @return `true` if registration is successful, `false` if the given block entity already has an interaction data provider
 */
fun registerInteractionDataFactory(entityType: Class<out Entity>, factory: IEntityInteractionDataFactory): Boolean {
    if (entityType in entityFactories) return false

    entityFactories[entityType] = factory
    return true
}

/**
 * Registers an interaction data factory for a block entity.
 *
 * @param T The block entity type the factory converts from
 * @param factory The block entity interaction data factory
 * @return `true` if registration is successful, `false` if the given block entity already has an interaction data provider
 */
inline fun <reified T : Entity> registerInteractionDataFactory(factory: IEntityInteractionDataFactory) =
    registerInteractionDataFactory(T::class.java, factory)
