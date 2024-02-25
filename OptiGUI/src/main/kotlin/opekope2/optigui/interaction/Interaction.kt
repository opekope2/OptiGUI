package opekope2.optigui.interaction

import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import opekope2.optigui.internal.TextureReplacer

/**
 * Interaction representation with a block entity or entity.
 *
 * @param texture The texture to be replaced.
 * @param screenTitle The active GUI screen's title
 * @param rawInteraction The raw interaction from Minecraft containing the details. See [RawInteraction] documentation
 * @param data The interaction data returned by the interaction data provider
 *
 * @see IBlockEntityProcessor
 * @see IEntityProcessor
 */
data class Interaction(
    val texture: Identifier,
    val screenTitle: Text,
    val rawInteraction: RawInteraction?,
    val data: Any?
) {
    companion object {
        /**
         * Prepares OptiGUI texture replacer for an interaction. Must be called before a [Screen] is opened.
         * If called multiple times before a [Screen] is opened, the last call takes effect.
         *
         * @param player The interacting player
         * @param world The world the interaction happens in. Must be client world
         * @param hand The hand of the player that triggered the interaction
         * @param target The target the player is interacting with
         * @param hitResult The hit result from Minecraft
         * @return `true` if the parameters are valid, and a GUI is not open, otherwise `false`
         */
        @JvmStatic
        fun prepare(
            player: PlayerEntity,
            world: World,
            hand: Hand,
            target: IInteractionTarget,
            hitResult: HitResult?
        ): Boolean {
            if (!world.isClient) throw IllegalArgumentException("World must be client world")
            return TextureReplacer.prepareInteraction(target, RawInteraction(player, world, hand, hitResult))
        }
    }
}
