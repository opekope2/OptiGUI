package opekope2.optigui.internal

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier
import opekope2.optigui.filter.IFilter
import opekope2.optigui.interaction.IInteractionTarget
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.interaction.RawInteraction
import opekope2.optigui.registry.EntityProcessorRegistry
import opekope2.optigui.registry.RetexturableScreenRegistry

internal object TextureReplacer : ClientModInitializer {
    private object InteractionHolder : ClientTickEvents.EndWorldTick, ClientPlayConnectionEvents.Disconnect {
        val replacementCache = mutableMapOf<Identifier, Identifier>()

        var interacting: Boolean = false
            private set

        private var target: IInteractionTarget? = null

        private var raw: RawInteraction? = null
        private var data: Any? = null
        private var screen: Screen? = null

        var riddenEntity: Entity? = null

        fun refreshInteractionData() {
            val newData = target?.computeInteractionData() ?: riddenEntity?.let(EntityProcessorRegistry::processEntity)

            if (newData != data) {
                data = newData
                replacementCache.clear()
            }
        }

        fun prepare(target: IInteractionTarget, rawInteraction: RawInteraction): Boolean {
            if (interacting) return false

            this.target = target
            this.raw = rawInteraction

            return true
        }

        fun begin(screen: Screen) {
            this.screen = screen

            interacting = true

            refreshInteractionData()
        }

        fun end() {
            target = null
            raw = null
            data = null

            interacting = false

            replacementCache.clear()
        }

        fun createInteraction(texture: Identifier): Interaction? {
            return if (!interacting) null
            else Interaction(texture, screen?.title ?: return null, raw, data)
        }

        override fun onEndTick(world: ClientWorld?) {
            if (interacting) {
                refreshInteractionData()
            }
        }

        override fun onPlayDisconnect(handler: ClientPlayNetworkHandler?, client: MinecraftClient?) {
            // Clean up, don't leak memory. Just to be safe.
            end()
            riddenEntity = null
        }
    }

    internal var filter: IFilter<Interaction, Identifier> = IFilter { IFilter.Result.Skip }
    internal var replaceableTextures = mutableSetOf<Identifier>()

    @JvmStatic
    var riddenEntity: Entity? by InteractionHolder::riddenEntity

    override fun onInitializeClient() {
        ClientTickEvents.END_WORLD_TICK.register(InteractionHolder)
        ClientPlayConnectionEvents.DISCONNECT.register(InteractionHolder)
    }

    @JvmStatic
    fun replaceTexture(texture: Identifier): Identifier {
        // Only replace predefined textures
        if (texture !in replaceableTextures) return texture

        // Don't bother replacing textures if not interacting
        val interaction = InteractionHolder.createInteraction(texture) ?: return texture

        return InteractionHolder.replacementCache.computeIfAbsent(texture) {
            filter.evaluate(interaction).let { (it as? IFilter.Result.Match)?.result } ?: texture
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

    fun prepareInteraction(target: IInteractionTarget, rawInteraction: RawInteraction): Boolean =
        InteractionHolder.prepare(target, rawInteraction)

    @JvmStatic
    fun refreshInteractionData() = InteractionHolder.refreshInteractionData()
}
