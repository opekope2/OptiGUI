package opekope2.optigui.interaction

import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.InteractionHand.MAIN_HAND
import net.minecraft.world.entity.player.Player
import opekope2.optigui.config.config
import opekope2.optigui.filter.texture_changer.TextureChangerFilter
import opekope2.optigui.interaction.InteractionManager.interaction
import opekope2.optigui.internal.TextStyler
import opekope2.optigui.internal.TextureChanger
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen
import org.jetbrains.annotations.ApiStatus

/**
 * Manages player interactions that have GUI interactions.
 */
object InteractionManager {
    private var screen: ITextureChangeableScreen? = null
    private var nextInteractionFactory: IInteraction.IFactory? = null

    /**
     * Returns if an interaction is ongoing.
     */
    @JvmStatic
    val isInteracting: Boolean
        get() = screen != null

    /**
     * Returns the ongoing interaction or `null`, if no interaction is ongoing.
     */
    @JvmStatic
    var interaction: IInteraction? = null
        private set

    /**
     * Returns the filter which matches the current [interaction] and changes its textures and sprites or `null`, if no
     * interaction is ongoing.
     */
    @JvmStatic
    val textureChangerFilter: TextureChangerFilter?
        get() = TextureChanger.filter.takeIf { isInteracting }

    /**
     * Tells OptiGUI the details about the next interaction. If called multiple times before a [Screen] is opened, the
     * last call takes effect. If called while a [Screen] is open, it will only take effect when the next [Screen] is
     * opened.
     */
    @JvmStatic
    fun prepare(factory: IInteraction.IFactory) {
        nextInteractionFactory = factory
    }

    /**
     * @suppress
     */
    @JvmStatic
    @JvmName("begin")
    @ApiStatus.Internal
    internal fun begin(screen: ITextureChangeableScreen, player: Player) {
        interaction = nextInteractionFactory?.apply(screen)
            ?: GeneralInteraction(screen, player.mainHandItem, InteractionTarget.Unknown, player, MAIN_HAND)
        this.screen = screen
        clearCache()
    }

    /**
     * @suppress
     */
    @JvmStatic
    @JvmOverloads
    @ApiStatus.Internal
    fun end(disconnected: Boolean = false) {
        interaction = null
        screen = null
        clearCache(disconnected)
        if (!config.keepInteractionFactory() || disconnected) nextInteractionFactory = null
    }

    /**
     * Clears the interaction cache. Call this if the current screen pauses the game in single player, after its content
     * gets updated.
     */
    @JvmStatic
    @JvmOverloads
    fun clearCache(disconnected: Boolean = false) {
        TextureChanger.clearCache()
        TextStyler.clearCache(disconnected)
    }
}
