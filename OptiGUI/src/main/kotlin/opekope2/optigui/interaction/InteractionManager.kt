package opekope2.optigui.interaction

import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.Identifier
import opekope2.optigui.filter.texture_changer.TextureChangerFilter
import opekope2.optigui.interaction.InteractionManager.clearCache
import opekope2.optigui.interaction.InteractionManager.interaction
import opekope2.optigui.interaction.data.IInteractionData
import opekope2.optigui.internal.TextureChanger
import opekope2.optigui.screen.ITextureChangeableScreen
import java.util.*

/**
 * Manages player interactions that have GUI interactions.
 */
object InteractionManager {
    private var screen: ITextureChangeableScreen? = null
    private var nextInteractionData: IInteractionData? = null

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
    var interaction: Interaction? = null
        private set

    /**
     * Returns the filter which matches the current [interaction] and changes its textures and sprites or `null`, if no
     * interaction is ongoing.
     */
    @JvmStatic
    val textureChangerFilter: TextureChangerFilter?
        get() = TextureChanger.filter.takeIf { isInteracting }

    /**
     * Returns the non-changed textures rendered since the previous call to [clearCache] or world tick (whichever was
     * later). This may include textures rendered throughout multiple frames.
     */
    @JvmStatic
    val renderedTextures: Set<Identifier> = Collections.unmodifiableSet(TextureChanger.renderedTextures)

    /**
     * Returns the non-changed sprites rendered since the previous call to [clearCache] or world tick (whichever was
     * later). This may include sprites rendered throughout multiple frames.
     */
    @JvmStatic
    val renderedSprites: Set<Identifier> = Collections.unmodifiableSet(TextureChanger.renderedSprites)

    /**
     * Returns if custom textures were rendered since the previous call to [clearCache] or world tick (whichever was
     * later). This may include textures rendered throughout multiple frames.
     */
    @JvmStatic
    val hasRenderedCustomTextures: Boolean
        @JvmName("hasRenderedCustomTextures")
        get() = TextureChanger.renderedCustomTextures

    /**
     * Tells OptiGUI the details about the next interaction. If called multiple times before a [Screen] is opened, the
     * last call takes effect. If called while a [Screen] is open, it will only take effect when the next [Screen] is
     * opened.
     */
    @JvmStatic
    fun prepare(data: IInteractionData) {
        nextInteractionData = data
    }

    /**
     * @suppress
     */
    @JvmStatic
    internal fun begin(screen: ITextureChangeableScreen) {
        // TODO handle screen change (no end() between two begin()s)
        interaction = nextInteractionData?.let { Interaction(screen, it) }
        nextInteractionData = null
        this.screen = screen
        clearCache()
    }

    /**
     * @suppress
     */
    @JvmStatic
    @JvmOverloads
    internal fun end(disconnected: Boolean = false) {
        interaction = null
        screen = null
        clearCache()
        if (disconnected) nextInteractionData = null
    }

    /**
     * Clears the interaction cache. Call this if the current screen pauses the game in single player, after its content
     * gets updated.
     */
    @JvmStatic
    fun clearCache() {
        TextureChanger.clearCache()
    }
}
