package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.Tag
import net.minecraft.world.level.block.state.BlockState
import opekope2.optigui.interaction.BlockInteraction
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.util.encode

/**
 * Provides the block state NBT of an interaction.
 */
object BlockStateNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: HolderLookup.Provider): Tag? {
        val data = interaction as? BlockInteraction ?: return null
        return encode(data.blockState, BlockState.CODEC, lookup)
    }
}
