package opekope2.optigui.interaction.nbt_provider

import net.minecraft.nbt.NbtString
import net.minecraft.registry.RegistryWrapper
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the biome identifier of an interaction.
 */
object BiomeIdNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup): NbtString =
        NbtString.of(interaction.world.getBiome(interaction.blockPos).idAsString)
}
