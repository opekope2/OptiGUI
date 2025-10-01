package opekope2.optigui.interaction.nbt_provider

import net.minecraft.nbt.NbtElement
import net.minecraft.registry.RegistryWrapper
import net.minecraft.world.biome.Biome
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.util.encode

/**
 * Provides the biome NBT of an interaction.
 */
object BiomeNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup): NbtElement {
        return encode(interaction.world.getBiome(interaction.blockPos).value(), Biome.CODEC, lookup)
    }
}
