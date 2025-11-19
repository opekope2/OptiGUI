package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.screen_api.util.NbtUtil

/**
 * Provides the block position NBT of an interaction.
 */
object PositionNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: HolderLookup.Provider) =
        NbtUtil.encode(interaction.blockPos, BlockPos.CODEC, lookup)
}
