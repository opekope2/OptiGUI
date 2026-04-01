package opekope2.optigui.internal

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.InteractionHand
import net.minecraft.resources.ResourceLocation
import opekope2.optigui.interaction.IBeforeInteractionBeginCallback
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.filter.ContainerMapFilter
import opekope2.optigui.registry.ContainerDefaultGuiTextureRegistry
import opekope2.optigui.registry.RetexturableScreenRegistry
import opekope2.optigui.util.identifier

internal object TextureReplacer : ClientModInitializer {
    private object InteractionHolder : ClientTickEvents.EndWorldTick, ClientPlayConnectionEvents.Join,
        ClientPlayConnectionEvents.Disconnect {
        val replacementCache = mutableMapOf<ResourceLocation, ResourceLocation>()

        var interacting: Boolean = false
            private set

        var container: ResourceLocation? = null
            private set
        var data: Interaction.Data? = null
            private set
        private var screen: Screen? = null

        fun prepare(container: ResourceLocation, data: Interaction.Data): Boolean {
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

        fun createInteraction(texture: ResourceLocation): Interaction? {
            return if (!interacting) null
            else Interaction(
                container ?: data?.player?.vehicle?.identifier ?: return null,
                texture,
                screen!!,
                data ?: return null
            )
        }

        override fun onEndTick(world: ClientLevel?) {
            if (interacting) {
                replacementCache.clear()
            }
        }

        override fun onPlayReady(handler: ClientPacketListener, sender: PacketSender?, client: Minecraft) {
            data = Interaction.Data(client.player!!, handler.level, InteractionHand.MAIN_HAND, null, null, null, null)
        }

        override fun onPlayDisconnect(handler: ClientPacketListener?, client: Minecraft?) {
            // Clean up, don't leak memory. Just to be safe.
            end()
            data = null
        }
    }

    private var filter: ContainerMapFilter = ContainerMapFilter(mapOf())
    private var replaceableTextures: Set<ResourceLocation> = setOf()

    val inspectableInteraction: Interaction?
        get() {
            return InteractionHolder.createInteraction(
                ContainerDefaultGuiTextureRegistry[InteractionHolder.container ?: return null] ?: return null
            )
        }
    val interactionData: Interaction.Data? by InteractionHolder::data

    @JvmStatic
    var isReplacingTextures = false
        get() = field && InteractionHolder.interacting

    override fun onInitializeClient() {
        ClientTickEvents.END_WORLD_TICK.register(InteractionHolder)
        ClientPlayConnectionEvents.JOIN.register(InteractionHolder)
        ClientPlayConnectionEvents.DISCONNECT.register(InteractionHolder)
    }

    @JvmStatic
    fun replaceTexture(texture: ResourceLocation): ResourceLocation {
        // Only replace predefined textures
        if (texture !in replaceableTextures) return texture

        // Don't bother replacing textures if not interacting
        val interaction = InteractionHolder.createInteraction(texture) ?: return texture

        return InteractionHolder.replacementCache.getOrPut(texture) {
            filter.evaluate(interaction) ?: texture
        }
    }

    @JvmStatic
    fun handleScreenChange(screen: Screen?) {
        when (screen) {
            null -> InteractionHolder.end()
            in RetexturableScreenRegistry -> InteractionHolder.begin(screen)
            else -> InteractionHolder.end()
        }
    }

    fun prepareInteraction(container: ResourceLocation, data: Interaction.Data): Boolean =
        InteractionHolder.prepare(container, data)

    fun onFiltersLoaded(filter: ContainerMapFilter, replaceableTextures: Set<ResourceLocation>) {
        this.filter = filter
        this.replaceableTextures = replaceableTextures
    }

    fun clearReplacementCache() = InteractionHolder.replacementCache.clear()
}
