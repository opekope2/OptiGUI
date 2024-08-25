package opekope2.optigui.interaction

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import opekope2.optigui.screen.IRetexturableScreen
import java.util.function.Supplier

/**
 * Interaction between a player and a container.
 *
 * @param originalTexture The texture to be replaced.
 * @param screen The active GUI screen
 * @param data The details of the interaction
 */
data class Interaction(
    val originalTexture: Identifier,
    val screen: IRetexturableScreen,
    val data: IInteractionData
) {
    /**
     * Details about the interacting player.
     */
    val playerData: PlayerData
        get() = data.playerData

    /**
     * Extra details about the interaction. May be mutable.
     */
    val extraData: Supplier<NbtCompound>?
        get() = data.extraData

    /**
     * Details about an interacting player.
     *
     * @param player The interacting player
     * @param hand The hand the player interacted with
     */
    data class PlayerData(val player: PlayerEntity, val hand: Hand) {
        /**
         * The entity the player is sitting in/on or `null`, if it's not sitting in/on anything.
         */
        val vehicle: Entity?
            get() = player.vehicle
    }
}
