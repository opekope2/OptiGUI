package opekope2.optigui.interaction.data

import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import opekope2.optigui.util.identifier

/**
 * Details about a player interacting with the inventory.
 *
 * @param item The item the player interacted with
 * @param playerData Details about the player
 */
data class InventoryInteractionData(override val item: ItemStack, override val playerData: InteractionPlayerData) :
    IInteractionData {
    override val id: Identifier
        get() = playerData.player.identifier

    override val blockPos: BlockPos
        get() = playerData.player.blockPos
}
