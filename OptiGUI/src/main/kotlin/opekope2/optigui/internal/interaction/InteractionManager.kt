package opekope2.optigui.internal.interaction

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.util.Identifier
import opekope2.optigui.interaction.AFTER_INTERACTION_END_EVENT
import opekope2.optigui.interaction.BEFORE_INTERACTION_BEGIN_EVENT
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.interaction.data.IInteractionData
import opekope2.optigui.internal.TextureReplacer
import opekope2.optigui.screen.IRetexturableScreen

internal object InteractionManager : ClientModInitializer, ClientPlayConnectionEvents.Disconnect {
    private var screen: IRetexturableScreen? = null

    @JvmStatic
    val isInteracting: Boolean
        get() = screen != null

    @JvmStatic
    var interactionData: IInteractionData? = null
        private set

    @JvmStatic
    fun prepare(data: IInteractionData) =
        if (isInteracting) false
        else {
            interactionData = data
            true
        }

    @JvmStatic
    fun begin(screen: IRetexturableScreen) {
        BEFORE_INTERACTION_BEGIN_EVENT.invoker().onBeforeInteractionBegin(screen)
        this.screen = screen
    }

    @JvmStatic
    fun end() {
        interactionData = null
        screen = null
        TextureReplacer.clearCache()
        AFTER_INTERACTION_END_EVENT.invoker().onAfterInteractionEnd()
    }

    @JvmStatic
    fun createInteraction(originalTexture: Identifier) =
        interactionData.takeIf { isInteracting }?.let { Interaction(originalTexture, screen!!, it) }

    override fun onInitializeClient() {
        ClientPlayConnectionEvents.DISCONNECT.register(this)
    }

    override fun onPlayDisconnect(handler: ClientPlayNetworkHandler?, client: MinecraftClient?) {
        end()
    }
}
