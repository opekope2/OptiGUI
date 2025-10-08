package opekope2.optigui.interaction.nbt_provider

import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.math.BlockPos
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.util.encode

/**
 * Provides the block position NBT of an interaction.
 */
object PositionNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup) =
        encode(interaction.blockPos, BlockPos.CODEC, lookup)
}
