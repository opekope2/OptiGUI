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

    /**
     * Returns if an interaction is ongoing.
     */
    @JvmStatic
    val isInteracting: Boolean
        get() = screen != null

    /**
     * Returns the interaction data last supplied using [prepare] or `null`, if the interaction has ended since.
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
     * Tells OptiGUI the details about the next interaction. Must be called before a [Screen] is opened.
     * If called multiple times before a [Screen] is opened, the last call takes effect.
     *
     * @return `true` if a GUI is not open, otherwise `false`
     */
    @JvmStatic
    fun prepare(data: IInteractionData) =
        if (isInteracting) false
        else {
            interactionData = data
            true
        }

    /**
     * @suppress
     */
    @JvmStatic
    internal fun begin(screen: IRetexturableScreen) {
        this.screen = screen
    }

    /**
     * @suppress
     */
    @JvmStatic
    internal fun end() {
        interactionData = null
        screen = null
        TextureReplacer.clearCache()
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
    }
}
