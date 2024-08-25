package opekope2.optigui.interaction

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import opekope2.optigui.util.identifier
import java.util.function.Supplier

/**
 * Details about a player interacting with the inventory.
 *
 * @param item The item the player interacted with
 * @param playerData Details about the player
 * @param extraData Extra details about the interaction. May be mutable
 */
data class InventoryInteractionData @JvmOverloads constructor(
    override val item: ItemStack,
    override val playerData: Interaction.PlayerData,
    override val extraData: Supplier<NbtCompound>? = null
) : IInteractionData {
    override val id: Identifier
        get() = playerData.player.identifier

    override val blockPos: BlockPos
        get() = playerData.player.blockPos
}
