package opekope2.optigui.interaction

import net.minecraft.client.gui.screen.Screen
import opekope2.optigui.internal.TextureReplacer
import opekope2.optigui.internal.interaction.InteractionManager as InternalInteractionManager

/**
 * Manages player interactions that have GUI interactions.
 */
object InteractionManager {
    /**
     * Returns if an interaction is ongoing.
     */
    @JvmStatic
    val isInteracting by InternalInteractionManager::isInteracting

    /**
     * Returns the interaction data last supplied using [prepare] or `null`, if the interaction has ended since.
     */
    @JvmStatic
    val interactionData by InternalInteractionManager::interactionData

    /**
     * Tells OptiGUI the details about the next interaction. Must be called before a [Screen] is opened.
     * If called multiple times before a [Screen] is opened, the last call takes effect.
     *
     * @return `true` if a GUI is not open, otherwise `false`
     * @see IBeforeInteractionBeginCallback
     */
    @JvmStatic
    fun prepare(data: IInteractionData) = InternalInteractionManager.prepare(data)

    /**
     * Clears the texture replacer cache. Call this after modifying [IInteractionData.extraData] if the current screen
     * pauses the game in single player.
     */
    @JvmStatic
    fun clearCache() {
        TextureReplacer.clearCache()
    }
}
