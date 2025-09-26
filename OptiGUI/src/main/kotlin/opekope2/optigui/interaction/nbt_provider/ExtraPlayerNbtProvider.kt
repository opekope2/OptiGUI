package opekope2.optigui.interaction.nbt_provider

import net.minecraft.client.MinecraftClient
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.world.GameMode
import net.minecraft.world.biome.Biome
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.util.encode

/**
 * Provides extra player NBT for an interaction.
 */
object ExtraPlayerNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: RegistryWrapper.WrapperLookup) = NbtCompound().apply {
        val player = interaction.player
        put("biome", encode(interaction.world.getBiome(interaction.player.blockPos).value(), Biome.CODEC, lookup))
        putString("biome_id", player.world.getBiome(player.blockPos).idAsString)
        MinecraftClient.getInstance().interactionManager?.let {
            put("game_mode", encode(it.currentGameMode, GameMode.CODEC, lookup))
        }
        putString("name", player.name.string)
        // TODO structures
    }
}
