package opekope2.optigui.internal

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.Identifier
import opekope2.optigui.internal.filter.TextureReplacerFilter
import opekope2.optigui.internal.interaction.InteractionManager
import opekope2.optigui.internal.util.OrderedListLruAccessor

internal typealias ContainerId2FiltersMap = ImmutableMap<Identifier?, OrderedListLruAccessor<TextureReplacerFilter>>

internal object TextureReplacer : ClientModInitializer, ClientTickEvents.EndWorldTick, ScreenEvents.BeforeInit,
    ScreenEvents.BeforeRender, ScreenEvents.AfterRender {
    private var filters: ContainerId2FiltersMap = ImmutableMap.of()
    private var replaceableTextures: ImmutableSet<Identifier> = ImmutableSet.of()
    private val replacementCache = mutableMapOf<Identifier, Identifier>()
    private var renderingScreen = false

    @JvmStatic
    fun loadFilters(filters: ContainerId2FiltersMap, replaceableTextures: ImmutableSet<Identifier>) {
        this.filters = filters
        this.replaceableTextures = replaceableTextures
    }

    @JvmStatic
    fun replaceTexture(texture: Identifier): Identifier {
        if (!renderingScreen) return texture
        if (!InteractionManager.isInteracting) return texture
        if (texture !in replaceableTextures) return texture

        val interaction = InteractionManager.createInteraction(texture) ?: return texture
        return replacementCache.getOrPut(texture) {
            filters[interaction.data.id]?.promoteFirstOrNull { it.test(interaction) }?.replacementTexture
                ?: filters[null]?.promoteFirstOrNull { it.test(interaction) }?.replacementTexture
                ?: texture
        }
    }

    fun clearCache() {
        replacementCache.clear()
    }

    override fun onInitializeClient() {
        ClientTickEvents.END_WORLD_TICK.register(this)
        ScreenEvents.BEFORE_INIT.register(this)
    }

    override fun onEndTick(world: ClientWorld?) {
        if (!InteractionManager.isInteracting) return
        clearCache()
    }

    override fun beforeInit(client: MinecraftClient?, screen: Screen, scaledWidth: Int, scaledHeight: Int) {
        ScreenEvents.beforeRender(screen).register(this)
        ScreenEvents.afterRender(screen).register(this)
    }

    override fun beforeRender(screen: Screen?, drawContext: DrawContext?, mouseX: Int, mouseY: Int, tickDelta: Float) {
        renderingScreen = true
    }

    override fun afterRender(screen: Screen?, drawContext: DrawContext?, mouseX: Int, mouseY: Int, tickDelta: Float) {
        renderingScreen = false
    }
}
