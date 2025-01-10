package opekope2.optigui.interaction.data

import net.minecraft.entity.Entity
import net.minecraft.entity.RideableInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Hand
import opekope2.optigui.util.INbtConvertible
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

    override fun writeNbt(compound: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        player.writeNbt(compound.subCompound("player"))
        vehicle?.writeNbt(compound.subCompound("vehicle"))
        compound.putString("player_biome", player.world.getBiomeId(player.blockPos).toString())
        compound.putString("hand", hand.name)
    }
}
