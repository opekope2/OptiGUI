package opekope2.optigui.service

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import opekope2.optigui.interaction.InteractionTarget

/**
 * Interface for triggering interactions for texture replacing
 */
interface InteractionService {
    /**
     * Signals the texture replacer that an interaction has begun. Should be called before opening a GUI.
     *
     * @param player The interacting player
     * @param world The world the interaction happens in
     * @param hand The hand of the player that triggered the interaction
     * @param target The target the player is interacting with
     * @param hitResult The hit result from Minecraft
     */
    fun interact(player: PlayerEntity, world: World, hand: Hand, target: InteractionTarget, hitResult: HitResult?): Boolean
}
