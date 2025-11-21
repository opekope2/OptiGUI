package opekope2.optigui.interaction.nbt_provider

import net.minecraft.client.multiplayer.MultiPlayerGameMode
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.GameType
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.screen_api.util.NbtUtil
import opekope2.optigui.util.mc

/**
 * Provides extra player NBT for an interaction.
 */
object PlayerNbtProvider : IInteractionNbtProvider {
    private val biomeProvider = BiomeNbtProvider { it.player.blockPosition() }
    private val biomeIdProvider = BiomeIdNbtProvider { it.player.blockPosition() }
    private val entityProvider = EntityNbtProvider(IInteraction::player)
    private val positionProvider = PositionNbtProvider { it.player.blockPosition() }
    private val vehicleProvider = EntityNbtProvider { it.player.vehicle }
    private val structureProvider = StructureBoundingBoxProvider { it.player.blockPosition() }

    private inline val gameMode: MultiPlayerGameMode
        get() = requireNotNull(mc.gameMode) { "Minecraft.gameMode was null" }

    override fun get(interaction: IInteraction, registryAccess: RegistryAccess) = CompoundTag().apply {
        put("biome", biomeProvider.get(interaction, registryAccess))
        put("biome_registration", biomeIdProvider.get(interaction, registryAccess))
        put("entity", entityProvider.get(interaction, registryAccess)!!)
        put("game_mode", NbtUtil.encode(gameMode.playerMode, GameType.CODEC, registryAccess))
        putString("name", interaction.player.name.string)
        put("pos", positionProvider.get(interaction, registryAccess))
        structureProvider.get(interaction, registryAccess)?.let { put("structures", it) }
        vehicleProvider.get(interaction, registryAccess)?.let { put("vehicle", it) }
    }
}
