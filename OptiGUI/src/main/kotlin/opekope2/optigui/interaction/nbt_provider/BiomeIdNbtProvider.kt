package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.StringTag
import opekope2.optigui.interaction.IInteraction

/**
 * Provides the biome identifier of an interaction.
 */
object BiomeIdNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: HolderLookup.Provider) =
        StringTag.valueOf(interaction.world.getBiome(interaction.blockPos).registeredName)
}
