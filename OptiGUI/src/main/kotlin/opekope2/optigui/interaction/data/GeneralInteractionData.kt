package opekope2.optigui.interaction.data

import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import opekope2.optigui.interaction.IInteractionTarget

/**
 * Details about a general player interaction.
 *
 * @param item The item the player interacted with
 * @param playerData Details about the player
 * @param target The target of the interaction
 */
data class GeneralInteractionData(
    override val item: ItemStack,
    override val playerData: InteractionPlayerData,
    override val target: IInteractionTarget
) : IInteractionData {
    override val blockPos: BlockPos
        get() = playerData.player.blockPos
}
