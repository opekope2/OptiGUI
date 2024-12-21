package opekope2.optigui.internal

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.world.ClientWorld
import net.minecraft.nbt.NbtElement
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.filter.TextureReplacerFilter
import opekope2.optigui.internal.interaction.InteractionManager
import opekope2.optigui.util.LinkedLruCollection
import opekope2.optigui.util.MOD_ID

internal typealias ContainerId2FiltersMap = ImmutableMap<Identifier?, LinkedLruCollection<TextureReplacerFilter, NbtElement>>

internal object TextureReplacer : SimpleSynchronousResourceReloadListener, ClientModInitializer,
    ClientTickEvents.EndWorldTick, ScreenEvents.BeforeInit, ScreenEvents.BeforeRender, ScreenEvents.AfterRender {
    private var filters: ContainerId2FiltersMap = ImmutableMap.of()
    private var replaceableTextures: ImmutableSet<Identifier> = ImmutableSet.of()
    private val replacementCache = mutableMapOf<Identifier, Identifier>()
    private var renderingScreen = false

    @JvmStatic
    fun replaceTexture(texture: Identifier): Identifier {
        if (!renderingScreen) return texture
        if (!InteractionManager.isInteracting) return texture
        if (texture !in replaceableTextures) return texture

        val interaction = InteractionManager.createInteraction(texture) ?: return texture
        val interactionNbt = interaction.createNbt()
        return replacementCache.getOrPut(texture) {
            val replacements = filters[interaction.data.id]?.promoteFirstOrNull(interactionNbt)?.replacementTextures
                ?: filters[null]?.promoteFirstOrNull(interactionNbt)?.replacementTextures
                ?: ImmutableMap.of()

            replacements[texture] ?: texture
        }
    }

    fun clearCache() {
        replacementCache.clear()
    }

    override fun getFabricId(): Identifier = Identifier.of(MOD_ID, "texture_replacer")

    override fun getFabricDependencies() = IFilterLoader.map { it.key }

    override fun reload(manager: ResourceManager?) {
        val filters = IFilterLoader.flatMap { it.value.get() }

        this.filters = ImmutableMap.copyOf(
            filters.groupBy { it.container }.mapValues { (_, list) -> LinkedLruCollection(list) }
        )
        this.replaceableTextures = ImmutableSet.copyOf(filters.flatMap { it.replacementTextures.keys })
    }

    override fun onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this)
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
