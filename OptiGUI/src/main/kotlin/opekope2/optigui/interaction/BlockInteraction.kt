package opekope2.optigui.interaction

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen

/**
 * Details about an interaction with a block.
 *
 * @param screen The active GUI screen
 * @param blockPos The interaction position
 * @param blockState The block state at [blockPos]
 * @param blockEntity The block entity at [blockPos] or `null`, if there's no block entity
 * @param item The item the player interacted with
 * @param player The interacting player
 * @param hand The hand the player interacted with
 */
data class BlockInteraction(
    override val screen: ITextureChangeableScreen,
    override val blockPos: BlockPos,
    val blockState: BlockState,
    val blockEntity: BlockEntity?,
    override val item: ItemStack,
    override val player: Player,
    override val hand: InteractionHand
) : IInteraction {
    override val target = InteractionTarget.Block(blockState)

    companion object {
        /**
         * Creates a factory for [BlockInteraction].
         *
         * @param blockPos The interaction position
         * @param blockState The block state at [blockPos]
         * @param blockEntity The block entity at [blockPos] or `null`, if there's no block entity
         * @param item The item the player interacted with
         * @param player The interacting player
         * @param hand The hand the player interacted with
         */
        @JvmStatic
        fun factory(
            blockPos: BlockPos,
            blockState: BlockState,
            blockEntity: BlockEntity?,
            item: ItemStack,
            player: Player,
            hand: InteractionHand
        ) = IInteraction.IFactory {
            BlockInteraction(it, blockPos, blockState, blockEntity, item, player, hand)
        }

        /**
         * Creates a factory for [BlockInteraction] with [item] being the item in [player]'s [hand].
         *
         * @param blockPos The interaction position
         * @param blockState The block state at [blockPos]
         * @param blockEntity The block entity at [blockPos] or `null`, if there's no block entity
         * @param player The interacting player
         * @param hand The hand the player interacted with
         * @see Player.getItemInHand
         */
        @JvmStatic
        fun factory(
            blockPos: BlockPos,
            blockState: BlockState,
            blockEntity: BlockEntity?,
            player: Player,
            hand: InteractionHand
        ) = factory(blockPos, blockState, blockEntity, player.getItemInHand(hand), player, hand)
    }
}
