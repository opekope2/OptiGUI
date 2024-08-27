package opekope2.optigui.internal

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.Identifier
import opekope2.optigui.internal.filter.TextureReplacerFilter
import opekope2.optigui.internal.interaction.InteractionManager
import opekope2.optigui.internal.util.OrderedListLruAccessor

internal typealias ContainerId2FiltersMap = ImmutableMap<Identifier?, OrderedListLruAccessor<TextureReplacerFilter>>

internal object TextureReplacer : ClientModInitializer, ClientTickEvents.EndWorldTick {
    private var filters: ContainerId2FiltersMap = ImmutableMap.of()
    private var replaceableTextures: ImmutableSet<Identifier> = ImmutableSet.of()
    private val replacementCache = mutableMapOf<Identifier, Identifier>()

    @JvmStatic
    var isReplacingTextures = false

    @JvmStatic
    fun loadFilters(filters: ContainerId2FiltersMap, replaceableTextures: ImmutableSet<Identifier>) {
        this.filters = filters
        this.replaceableTextures = replaceableTextures
    }

    @JvmStatic
    fun replaceTexture(texture: Identifier): Identifier {
        // Only replace predefined textures
        if (texture !in replaceableTextures) return texture

        // Don't bother replacing textures if not interacting
        val interaction = InteractionManager.createInteraction(texture) ?: return texture

        return replacementCache.getOrPut(texture) {
            filters[interaction.data.id]?.promoteFirstOrNull { it.test(interaction) }?.replacementTexture
                ?: filters[null]?.promoteFirstOrNull { it.test(interaction) }?.replacementTexture
                ?: texture
        }
    }

    @JvmStatic
    fun clearCache() {
        replacementCache.clear()
    }

    override fun onInitializeClient() {
        ClientTickEvents.END_WORLD_TICK.register(this)
    }

    override fun onEndTick(world: ClientWorld?) {
        if (!InteractionManager.isInteracting) return
        clearCache()
    }
}
