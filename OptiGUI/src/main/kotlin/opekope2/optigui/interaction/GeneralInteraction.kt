package opekope2.optigui.interaction

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import opekope2.optigui.screen.ITextureChangeableScreen

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
    override val player: PlayerEntity,
    override val hand: Hand
) : IInteraction {
    override val blockPos: BlockPos
        get() = player.blockPos

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
        fun factory(item: ItemStack, target: InteractionTarget, player: PlayerEntity, hand: Hand) =
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
        fun factory(target: InteractionTarget, player: PlayerEntity, hand: Hand) =
            factory(player.getStackInHand(hand), target, player, hand)
    }
}
