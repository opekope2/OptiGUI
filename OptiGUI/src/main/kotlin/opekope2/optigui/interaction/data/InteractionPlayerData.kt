package opekope2.optigui.interaction.data

import net.minecraft.entity.Entity
import net.minecraft.entity.RideableInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand

/**
 * Details about an interacting player.
 *
 * @param player The interacting player
 * @param hand The hand the player interacted with
 */
data class InteractionPlayerData(val player: PlayerEntity, val hand: Hand) {
    /**
     * The entity the player is sitting in/on or `null`, if it's not sitting in/on anything.
     */
    val vehicle: Entity?
        get() = player.vehicle.takeIf { it is RideableInventory }
}
