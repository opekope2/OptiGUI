package opekope2.optigui.interaction

import net.minecraft.block.entity.BlockEntity as MC_BlockEntity
import net.minecraft.entity.Entity as MC_Entity

/**
 * Represents the target of an interaction. See nested classes for available options.
 */
sealed interface InteractionTarget {
    /**
     * Calculates the interaction data by the given target entity.
     *
     * @see Preprocessors.preprocessBlockEntity
     * @see Preprocessors.preprocessEntity
     */
    fun computeInteractionData(): Any?

    /**
     * Represents the target of an interaction as a block entity.
     *
     * @param blockEntity The target of the interaction
     */
    class BlockEntity(val blockEntity: MC_BlockEntity) : InteractionTarget {
        override fun computeInteractionData(): Any? = Preprocessors.preprocessBlockEntity(blockEntity)
    }

    /**
     * Represents the target of an interaction as an entity.
     *
     * @param entity The target of the interaction
     */
    class Entity(val entity: MC_Entity) : InteractionTarget {
        override fun computeInteractionData(): Any? = Preprocessors.preprocessEntity(entity)
    }

    /**
     * An interaction target, which gets computed on each game tick,
     * so be sure to write optimized code in order to avoid performance issues.
     *
     * @param compute The implementation of [computeInteractionData]
     */
    class Computed(val compute: () -> Any?) : InteractionTarget {
        override fun computeInteractionData(): Any? = compute()
    }

    /**
     * Represents an interaction without a target.
     * The interaction data returned by [computeInteractionData] will always be `null` as preprocessors are unavailable.
     */

    object None : InteractionTarget {
        override fun computeInteractionData(): Any? = null
    }

    /**
     * Represents an already processed interaction.
     *
     * @param interactionData The interaction data to be returned by [computeInteractionData]
     */
    class Preprocessed(private val interactionData: Any?) : InteractionTarget {
        override fun computeInteractionData(): Any? = interactionData
    }
}
