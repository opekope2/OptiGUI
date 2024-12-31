package opekope2.optigui.interaction

import com.google.common.collect.ImmutableSet
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.util.Identifier
import opekope2.optigui.interaction.data.IInteractionData
import opekope2.optigui.internal.TextureReplacer
import opekope2.optigui.screen.IRetexturableScreen

/**
 * Manages player interactions that have GUI interactions.
 */
internal object InteractionManager : ClientModInitializer, ClientPlayConnectionEvents.Disconnect {
    private var screen: IRetexturableScreen? = null
    private var nextInteractionData: IInteractionData? = null

    /**
     * Returns if an interaction is ongoing.
     */
    @JvmStatic
    val isInteracting: Boolean
        get() = screen != null

    /**
     * Returns the interaction data last supplied using [prepare] or `null`, if no interaction is ongoing.
     */
    @JvmStatic
    var interactionData: IInteractionData? = null
        private set

    /**
     * Returns the non-replaced textures rendered since the previous call to [clearCache] or world tick (whichever was
     * later). This may include textures rendered throughout multiple frames.
     */
    @JvmStatic
    val renderedTextures: ImmutableSet<Identifier>
        get() = ImmutableSet.copyOf(TextureReplacer.renderedTextures)

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
    internal fun begin(screen: IRetexturableScreen) {
        // TODO handle screen change (no end() between two begin()s)
        interactionData = nextInteractionData
        nextInteractionData = null
        this.screen = screen
    }

    /**
     * @suppress
     */
    @JvmStatic
    internal fun end() {
        interactionData = null
        screen = null
        clearCache()
    }

    @JvmStatic
    fun createInteraction(originalTexture: Identifier) =
        interactionData.takeIf { isInteracting }?.let { Interaction(originalTexture, screen!!, it) }

    /**
     * Clears the texture replacer cache. Call this if the current screen pauses the game in single player, when its
     * content gets updated.
     */
    @JvmStatic
    fun clearCache() {
        TextureReplacer.clearCache()
    }

    override fun onInitializeClient() {
        ClientPlayConnectionEvents.DISCONNECT.register(this)
    }

    override fun onPlayDisconnect(handler: ClientPlayNetworkHandler?, client: MinecraftClient?) {
        end()
        nextInteractionData = null
    }
}
