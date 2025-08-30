package opekope2.optigui.interaction.data

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.math.BlockPos
import opekope2.optigui.interaction.IInteractionTarget
import opekope2.optigui.util.encode

/**
 * Details about an interaction with a block.
 *
 * @param blockPos The interaction position
 * @param blockState The block state at [blockPos]
 * @param blockEntity The block entity at [blockPos] or `null`, if there's no block entity
 * @param item The item the player interacted with
 * @param playerData Details about the player
 */
data class BlockInteractionData(
    override val blockPos: BlockPos,
    val blockState: BlockState,
    val blockEntity: BlockEntity?,
    override val item: ItemStack,
    override val playerData: InteractionPlayerData
) : IInteractionData {
    override val target = IInteractionTarget.Block(blockState)

    override fun optiGui_writeNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        super.optiGui_writeNbt(compound, lookup)

        compound.encode("block_state", blockState, BlockState.CODEC, lookup)
        if (blockEntity != null) compound.put("block_entity", blockEntity.createNbtWithId(lookup))
    }
}
