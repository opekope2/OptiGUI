package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.util.encode

/**
 * Provides the block position NBT of an interaction.
 */
object PositionNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: HolderLookup.Provider) =
        encode(interaction.blockPos, BlockPos.CODEC, lookup)
}
