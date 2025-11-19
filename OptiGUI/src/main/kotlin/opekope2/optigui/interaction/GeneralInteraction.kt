package opekope2.optigui.interaction

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen

/**
 * Details about a general player interaction.
 *
 * @param screen The active GUI screen
 * @param item The item the player interacted with
 * @param target The target of the interaction
 * @param player The interacting player
 * @param hand The hand the player interacted with
 */
data class GeneralInteraction(
    override val screen: ITextureChangeableScreen,
    override val item: ItemStack,
    override val target: InteractionTarget,
    override val player: Player,
    override val hand: InteractionHand
) : IInteraction {
    override val blockPos: BlockPos
        get() = player.blockPosition()

    companion object {
        /**
         * Creates a factory for [GeneralInteraction].
         *
         * @param item The item the player interacted with
         * @param target The target of the interaction
         * @param player The interacting player
         * @param hand The hand the player interacted with
         */
        @JvmStatic
        fun factory(item: ItemStack, target: InteractionTarget, player: Player, hand: InteractionHand) =
            IInteraction.IFactory {
                GeneralInteraction(it, item, target, player, hand)
            }

        /**
         * Creates a factory for [GeneralInteraction] with [item] being the item in [player]'s [hand].
         *
         * @param target The target of the interaction
         * @param player The interacting player
         * @param hand The hand the player interacted with
         */
        @JvmStatic
        fun factory(target: InteractionTarget, player: Player, hand: InteractionHand) =
            factory(player.getItemInHand(hand), target, player, hand)
    }
}
