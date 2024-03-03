package opekope2.optigui.internal

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import opekope2.optigui.filter.IFilter.Result.Match
import opekope2.optigui.interaction.IBeforeInteractionBeginCallback
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.filter.ContainerMapFilter
import opekope2.optigui.registry.RetexturableScreenRegistry
import opekope2.optigui.util.identifier

internal object TextureReplacer : ClientModInitializer {
    private object InteractionHolder : ClientTickEvents.EndWorldTick, ClientPlayConnectionEvents.Join,
        ClientPlayConnectionEvents.Disconnect {
        val replacementCache = mutableMapOf<Identifier, Identifier>()

        var interacting: Boolean = false
            private set

        private var container: Identifier? = null
        var data: Interaction.Data? = null
            private set
        private var screen: Screen? = null

        fun prepare(container: Identifier, data: Interaction.Data): Boolean {
            if (interacting) return false

            this.container = container
            this.data = data

            return true
        }

        fun begin(screen: Screen) {
            IBeforeInteractionBeginCallback.EVENT.invoker().onBeforeBegin(screen)

            this.screen = screen

            interacting = true
        }

        fun end() {
            container = null
            data = data?.copy(hitResult = null, extra = null, blockEntity = null, entity = null)

            interacting = false

            replacementCache.clear()
        }

        fun createInteraction(texture: Identifier): Interaction? {
            return if (!interacting) null
            else Interaction(container ?: data?.player?.vehicle?.identifier ?: return null, texture, screen!!, data)
        }

        override fun onEndTick(world: ClientWorld?) {
            if (interacting) {
                replacementCache.clear()
            }
        }

        override fun onPlayReady(handler: ClientPlayNetworkHandler, sender: PacketSender?, client: MinecraftClient) {
            data = Interaction.Data(client.player!!, handler.world, Hand.MAIN_HAND, null, null, null, null)
        }

        override fun onPlayDisconnect(handler: ClientPlayNetworkHandler?, client: MinecraftClient?) {
            // Clean up, don't leak memory. Just to be safe.
            end()
            data = null
        }
    }

    private var filter: ContainerMapFilter = ContainerMapFilter(mapOf())
    private var replaceableTextures: Set<Identifier> = setOf()

    val interactionData: Interaction.Data? by InteractionHolder::data

    override fun onInitializeClient() {
        ClientTickEvents.END_WORLD_TICK.register(InteractionHolder)
        ClientPlayConnectionEvents.JOIN.register(InteractionHolder)
        ClientPlayConnectionEvents.DISCONNECT.register(InteractionHolder)
    }

    @JvmStatic
    fun replaceTexture(texture: Identifier): Identifier {
        // Only replace predefined textures
        if (texture !in replaceableTextures) return texture

        // Don't bother replacing textures if not interacting
        val interaction = InteractionHolder.createInteraction(texture) ?: return texture

        return filter.evaluate(interaction).let { (it as? Match)?.result } ?: texture
    }

    @JvmStatic
    fun handleScreenChange(screen: Screen?) {
        when (screen) {
            null -> InteractionHolder.end()
            in RetexturableScreenRegistry -> InteractionHolder.begin(screen)
            else -> InteractionHolder.end()
        }
    }

    fun prepareInteraction(container: Identifier, data: Interaction.Data): Boolean =
        InteractionHolder.prepare(container, data)

    internal fun onFiltersLoaded(filter: ContainerMapFilter, replaceableTextures: Set<Identifier>) {
        this.filter = filter
        this.replaceableTextures = replaceableTextures
    }
}
