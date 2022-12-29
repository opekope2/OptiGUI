package opekope2.optigui.interaction

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity

fun interface BlockEntityInteractionDataFactory {
    fun createInteractionData(blockEntity: BlockEntity): Any?
}

fun interface EntityInteractionDataFactory {
    fun createInteractionData(entity: Entity): Any?
}

internal val blockEntityFactories = mutableMapOf<Class<out BlockEntity>, BlockEntityInteractionDataFactory>()
internal val entityFactories = mutableMapOf<Class<out Entity>, EntityInteractionDataFactory>()

fun registerInteractionDataFactory(
    blockEntityType: Class<out BlockEntity>, factory: BlockEntityInteractionDataFactory
): Boolean {
    if (blockEntityType in blockEntityFactories) return false

    blockEntityFactories[blockEntityType] = factory
    return true
}

inline fun <reified T : BlockEntity> registerInteractionDataFactory(factory: BlockEntityInteractionDataFactory) =
    registerInteractionDataFactory(T::class.java, factory)

fun registerInteractionDataFactory(entityType: Class<out Entity>, factory: EntityInteractionDataFactory): Boolean {
    if (entityType in entityFactories) return false

    entityFactories[entityType] = factory
    return true
}

inline fun <reified T : Entity> registerInteractionDataFactory(factory: EntityInteractionDataFactory) =
    registerInteractionDataFactory(T::class.java, factory)
