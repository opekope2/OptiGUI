package opekope2.optigui.interaction

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import opekope2.optigui.util.identifier
import java.util.function.Supplier

/**
 * Details about an interaction with a container block.
 *
 * @param blockPos The interaction position
 * @param blockState The block state at [blockPos]
 * @param blockEntity The block entity at [blockPos] or `null`, if there's no block entity
 * @param item The item the player interacted with
 * @param playerData Details about the player
 * @param extraData Extra details about the interaction. May be mutable
 */
data class BlockInteractionData @JvmOverloads constructor(
    override val blockPos: BlockPos,
    val blockState: BlockState,
    val blockEntity: BlockEntity?,
    override val item: ItemStack,
    override val playerData: Interaction.PlayerData,
    override val extraData: Supplier<NbtCompound>? = null
) : IInteractionData {
    override val id: Identifier
        get() = blockState.block.identifier
}
