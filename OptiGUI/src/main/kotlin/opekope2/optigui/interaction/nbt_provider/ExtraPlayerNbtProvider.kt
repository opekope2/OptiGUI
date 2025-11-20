package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.GameType
import net.minecraft.world.level.biome.Biome
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.screen_api.util.NbtUtil
import opekope2.optigui.util.mc

/**
 * Provides extra player NBT for an interaction.
 */
object ExtraPlayerNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: HolderLookup.Provider) = CompoundTag().apply {
        val player = interaction.player
        val biome = interaction.world.getBiome(player.blockPosition())

        put("biome", NbtUtil.encode(biome.value(), Biome.DIRECT_CODEC, lookup))
        putString("biome_registration", biome.registeredName)
        mc.gameMode?.let { put("game_mode", NbtUtil.encode(it.playerMode, GameType.CODEC, lookup)) }
        putString("name", player.name.string)
        // TODO structures
    }
}
