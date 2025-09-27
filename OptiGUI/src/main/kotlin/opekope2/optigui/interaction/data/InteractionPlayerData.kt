package opekope2.optigui.interaction.data

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.RideableInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Hand
import net.minecraft.world.GameMode
import opekope2.optigui.util.INbtConvertible
import opekope2.optigui.util.encode
import opekope2.optigui.util.getBiomeId
import opekope2.optigui.util.subCompound

/**
 * Details about an interacting player.
 *
 * @param player The interacting player
 * @param hand The hand the player interacted with
 */
data class InteractionPlayerData(val player: PlayerEntity, val hand: Hand) : INbtConvertible {
    /**
     * The entity the player is sitting in/on or `null`, if it's not sitting in/on anything.
     */
    val vehicle: Entity?
        get() = player.vehicle.takeIf { it is RideableInventory }

    override fun optiGui_writeNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        player.writeNbt(compound.subCompound("player"))
        vehicle?.writeNbt(compound.subCompound("vehicle"))
        writeExtraNbt(compound.subCompound("player_extra"), lookup)
        compound.putString("hand", hand.name)
    }

    private fun writeExtraNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        compound.putString("biome", player.world.getBiomeId(player.blockPos).toString())
        compound.putString("name", player.name.string)
        MinecraftClient.getInstance().interactionManager?.let {
            compound.encode("game_mode", it.currentGameMode, GameMode.CODEC, lookup)
        }
    }
}
