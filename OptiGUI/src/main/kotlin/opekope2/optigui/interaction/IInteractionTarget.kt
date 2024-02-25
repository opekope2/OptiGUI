package opekope2.optigui.interaction

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import opekope2.optigui.registry.BlockEntityProcessorRegistry
import opekope2.optigui.registry.EntityProcessorRegistry

/**
 * Represents the target of an interaction. See nested classes for available options.
 */
sealed interface IInteractionTarget {
    /**
     * Calculates the interaction data by the given target entity.
     *
     * @see BlockEntityProcessorRegistry.processBlockEntity
     * @see EntityProcessorRegistry.processEntity
     */
    fun computeInteractionData(): Any?

    /**
     * Represents the target of an interaction as a block entity.
     *
     * @param blockEntity The target of the interaction
     */
    data class BlockEntityTarget(val blockEntity: BlockEntity) : IInteractionTarget {
        override fun computeInteractionData(): Any? = BlockEntityProcessorRegistry.processBlockEntity(blockEntity)
    }

    /**
     * Represents the target of an interaction as an entity.
     *
     * @param entity The target of the interaction
     */
    data class EntityTarget(val entity: Entity) : IInteractionTarget {
        override fun computeInteractionData(): Any? = EntityProcessorRegistry.processEntity(entity)
    }

    /**
     * An interaction target, which gets computed on each game tick,
     * so be sure to write optimized code in order to avoid performance issues.
     *
     * @param compute The implementation of [computeInteractionData]
     */
    data class ComputedTarget(val compute: () -> Any?) : IInteractionTarget {
        /**
         * Create a computed interaction target from an already computed value.
         *
         * @param interactionData The interaction data to be returned by [computeInteractionData]
         */
        constructor(interactionData: Any?) : this({ interactionData })

        override fun computeInteractionData(): Any? = compute()
    }

    companion object {
        /**
         * Represents an interaction without a target.
         * The interaction data returned by [computeInteractionData] will always be `null` as preprocessors are unavailable.
         */
        @JvmField
        val NoneTarget = ComputedTarget(null)
    }
}
