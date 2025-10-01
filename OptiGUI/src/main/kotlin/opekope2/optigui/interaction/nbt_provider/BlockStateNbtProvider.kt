package opekope2.optigui.interaction.nbt_provider

import net.minecraft.block.BlockState
import net.minecraft.nbt.NbtElement
import net.minecraft.registry.RegistryWrapper
import opekope2.optigui.interaction.BlockInteraction
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.util.encode

/**
 * Provides the block state NBT of an interaction.
 */
object BlockStateNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup): NbtElement? {
        val data = interaction as? BlockInteraction ?: return null
        return encode(data.blockState, BlockState.CODEC, lookup)
    }
}
