package opekope2.optigui.interaction

import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import opekope2.optigui.config.IConfig
import opekope2.optigui.filter.texture_changer.TextureChangerFilter
import opekope2.optigui.interaction.InteractionManager.clearCache
import opekope2.optigui.interaction.InteractionManager.interaction
import opekope2.optigui.internal.TextStyler
import opekope2.optigui.internal.TextureChanger
import opekope2.optigui.screen_api.screen.ITextureChangeableScreen
import opekope2.optigui.util.TextOrigin
import opekope2.optigui.util.collections.IEnumObjectPairSet
import org.jetbrains.annotations.ApiStatus
import java.util.*

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
     * Returns the strings rendered since the previous call to [clearCache] or world tick (whichever was later). This
     * may include strings rendered throughout multiple frames.
     */
    @JvmStatic
    val renderedStrings: IEnumObjectPairSet<TextOrigin, String> = TextStyler.renderedStrings.View()

    /**
     * Returns the texts rendered since the previous call to [clearCache] or world tick (whichever was later). This may
     * include texts rendered throughout multiple frames.
     */
    @JvmStatic
    val renderedTexts: IEnumObjectPairSet<TextOrigin, Text> = TextStyler.renderedTexts.View()

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
    fun prepare(factory: IInteraction.IFactory) {
        nextInteractionFactory = factory
    }

    /**
     * @suppress
     */
    @JvmStatic
    @JvmName("begin")
    @ApiStatus.Internal
    internal fun begin(screen: ITextureChangeableScreen, player: PlayerEntity) {
        if (this.screen != null) return
        interaction = nextInteractionFactory?.apply(screen)
            ?: GeneralInteraction(screen, player.mainHandStack, InteractionTarget.Unknown, player, Hand.MAIN_HAND)
        if (!IConfig.get().keepInteractionFactory) nextInteractionFactory = null
        this.screen = screen
        clearCache()
    }

    /**
     * @suppress
     */
    @JvmStatic
    @JvmOverloads
    @JvmName("end")
    @ApiStatus.Internal
    internal fun end(disconnected: Boolean = false) {
        interaction = null
        screen = null
        clearCache(disconnected)
        if (disconnected) nextInteractionFactory = null
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
