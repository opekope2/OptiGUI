package opekope2.optigui.interaction

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen

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
    override val player: Player,
    override val hand: InteractionHand
) : IInteraction {
    override val target = InteractionTarget.Entity(entity)

    override val blockPos: BlockPos
        get() = entity.blockPosition()

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
        fun factory(entity: Entity, item: ItemStack, player: Player, hand: InteractionHand) = IInteraction.IFactory {
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
        fun factory(entity: Entity, player: Player, hand: InteractionHand) =
            factory(entity, player.getItemInHand(hand), player, hand)
    }
}
