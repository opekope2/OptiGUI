package opekope2.optigui.interaction

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import opekope2.optigui.screen.ITextureChangeableScreen

/**
 * Details about an interaction with an entity.
 *
 * @param screen The active GUI screen
 * @param entity The entity the player interacted with
 * @param item The item the player interacted with
 * @param player The interacting player
 * @param hand The hand the player interacted with
 */
data class EntityInteraction(
    override val screen: ITextureChangeableScreen,
    val entity: Entity,
    override val item: ItemStack,
    override val player: PlayerEntity,
    override val hand: Hand
) : IInteraction {
    override val target = InteractionTarget.Entity(entity)

    override val blockPos: BlockPos
        get() = entity.blockPos

    companion object {
        /**
         * Creates a factory for [EntityInteraction].
         *
         * @param entity The entity the player interacted with
         * @param item The item the player interacted with
         * @param player The interacting player
         * @param hand The hand the player interacted with
         */
        @JvmStatic
        fun factory(entity: Entity, item: ItemStack, player: PlayerEntity, hand: Hand) = IInteraction.IFactory {
            EntityInteraction(it, entity, item, player, hand)
        }

        /**
         * Creates a factory for [EntityInteraction] with [item] being the item in [player]'s [hand].
         *
         * @param entity The entity the player interacted with
         * @param player The interacting player
         * @param hand The hand the player interacted with
         */
        @JvmStatic
        fun factory(entity: Entity, player: PlayerEntity, hand: Hand) =
            factory(entity, player.getStackInHand(hand), player, hand)
    }
}
