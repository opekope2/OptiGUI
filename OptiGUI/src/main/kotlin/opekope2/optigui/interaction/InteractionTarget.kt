package opekope2.optigui.interaction

import net.minecraft.block.entity.BlockEntity as MC_BlockEntity
import net.minecraft.entity.Entity as MC_Entity

sealed interface InteractionTarget {
    fun computeInteractionData(): Any?

    class BlockEntity(val blockEntity: MC_BlockEntity) : InteractionTarget {
        override fun computeInteractionData(): Any? = Preprocessors.preprocessBlockEntity(blockEntity)
    }

    class Entity(val entity: MC_Entity) : InteractionTarget {
        override fun computeInteractionData(): Any? = Preprocessors.preprocessEntity(entity)
    }

    object None : InteractionTarget {
        override fun computeInteractionData(): Any? = null
    }

    class Preprocessed(val interactionData: Any?) : InteractionTarget {
        override fun computeInteractionData(): Any? = interactionData
    }
}
