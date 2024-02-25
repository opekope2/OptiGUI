package opekope2.optigui.internal

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import opekope2.optigui.filter.IFilter
import opekope2.optigui.interaction.*
import opekope2.optigui.internal.service.RetexturableScreensRegistryService

internal object TextureReplacer : IInteractor {
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
            val newData = target?.computeInteractionData() ?: riddenEntity?.let(Preprocessors::preprocessEntity)

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

    private val retexturableScreens: RetexturableScreensRegistryService = RetexturableScreensRegistry // TODO

    internal var filter: IFilter<Interaction, Identifier> = IFilter { IFilter.Result.Skip }
    internal var replaceableTextures = mutableSetOf<Identifier>()

    @JvmStatic
    var riddenEntity: Entity? by InteractionHolder::riddenEntity

    init {
        ClientTickEvents.END_WORLD_TICK.register(InteractionHolder)
        ClientPlayConnectionEvents.DISCONNECT.register(InteractionHolder)
    }

    @JvmStatic
    fun replaceTexture(texture: Identifier): Identifier {
        // Don't bother replacing textures if not interacting
        val interaction = InteractionHolder.createInteraction(texture) ?: return texture

        // Only replace predefined textures
        if (texture !in replaceableTextures) return texture

        return InteractionHolder.replacementCache.computeIfAbsent(texture) {
            filter.evaluate(interaction).let { (it as? IFilter.Result.Match)?.result } ?: texture
        }
    }

    @JvmStatic
    fun handleScreenChange(screen: Screen?) {
        when {
            screen == null -> InteractionHolder.end()
            retexturableScreens.isScreenRetexturable(screen) -> InteractionHolder.begin(screen)
            else -> InteractionHolder.end()
        }
    }

    override fun interact(
        player: PlayerEntity,
        world: World,
        hand: Hand,
        target: IInteractionTarget,
        hitResult: HitResult?
    ): Boolean {
        if (!world.isClient) return false
        return InteractionHolder.prepare(target, RawInteraction(player, world, hand, hitResult))
    }

    @JvmStatic
    fun refreshInteractionData() = InteractionHolder.refreshInteractionData()
}
