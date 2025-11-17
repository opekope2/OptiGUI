package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.HolderLookup
import net.minecraft.world.level.biome.Biome
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.screen_api.util.NbtUtil

/**
 * Provides the biome NBT of an interaction.
 */
object BiomeNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: HolderLookup.Provider) =
        NbtUtil.encode(interaction.world.getBiome(interaction.blockPos).value(), Biome.DIRECT_CODEC, lookup)
}
