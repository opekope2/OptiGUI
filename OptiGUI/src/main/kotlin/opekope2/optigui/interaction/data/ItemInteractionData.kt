package opekope2.optigui.interaction.data

import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import opekope2.optigui.util.identifier

/**
 * Details about an interaction with a held item.
 *
 * @param item The item the player interacted with
 * @param playerData Details about the player
 */
data class ItemInteractionData(override val item: ItemStack, override val playerData: InteractionPlayerData) :
    IInteractionData {
    override val id: Identifier
        get() = item.item.identifier

    override val blockPos: BlockPos
        get() = playerData.player.blockPos
}
