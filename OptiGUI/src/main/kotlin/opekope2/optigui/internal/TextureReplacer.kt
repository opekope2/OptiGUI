package opekope2.optigui.internal

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import opekope2.filter.Filter
import opekope2.filter.FilterResult
import opekope2.optigui.interaction.*
import opekope2.optigui.service.InteractionService

internal object TextureReplacer : InteractionService {
    private val interactionHolder = object : ClientTickEvents.EndWorldTick, ClientPlayConnectionEvents.Disconnect {
        var interacting: Boolean = false
            private set

        private var target: InteractionTarget? = null

        private var raw: RawInteraction? = null
        private var data: Any? = null
        private var screen: HandledScreen<*>? = null

        var riddenEntity: Entity? = null
            set(value) {
                if (!interacting) field = value
            }

        private fun refreshInteractionData() {
            data = target?.computeInteractionData() ?: riddenEntity?.let(Preprocessors::preprocessEntity)
        }

        fun prepare(target: InteractionTarget, rawInteraction: RawInteraction): Boolean {
            if (interacting) return false

            this.target = target
            this.raw = rawInteraction

            return true
        }

        fun begin(screen: HandledScreen<*>) {
            this.screen = screen

            interacting = true

            refreshInteractionData()
        }

        fun end() {
            target = null
            raw = null
            data = null

            interacting = false
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

    internal var filter: Filter<Interaction, Identifier> = Filter { FilterResult.Skip() }
    internal var replaceableTextures = mutableSetOf<Identifier>()

    @JvmStatic
    var riddenEntity: Entity? by interactionHolder::riddenEntity

    init {
        ClientTickEvents.END_WORLD_TICK.register(interactionHolder)
        ClientPlayConnectionEvents.DISCONNECT.register(interactionHolder)
    }

    @JvmStatic
    fun replaceTexture(texture: Identifier): Identifier {
        // Don't bother replacing textures if not interacting
        if (!interactionHolder.interacting) return texture
        val interaction = interactionHolder.createInteraction(texture) ?: return texture

        // Only replace predefined textures
        if (texture !in replaceableTextures) return texture

        // TODO caching
        return filter.evaluate(interaction).let { (it as? FilterResult.Match)?.result ?: texture }
    }

    @JvmStatic
    fun handleScreenChange(screen: Screen?) {
        (screen as? HandledScreen<*>).let {
            if (it != null) {
                interactionHolder.begin(it)
            } else {
                interactionHolder.end()
            }
        }
    }

    override fun interact(
        player: PlayerEntity, world: World, hand: Hand, target: InteractionTarget, hitResult: HitResult?
    ): Boolean = interactionHolder.prepare(target, RawInteraction(player, world, hand, hitResult))
}
