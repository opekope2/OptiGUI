package opekope2.optigui.internal

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import net.minecraft.nbt.NbtElement
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.util.Identifier
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.filter.TextureReplacerFilter
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.util.LinkedLruCollection

internal typealias ContainerId2FiltersMap = ImmutableMap<Identifier?, LinkedLruCollection<TextureReplacerFilter, NbtElement>>

internal object TextureReplacer : SynchronousResourceReloader {
    private var filters: ContainerId2FiltersMap = ImmutableMap.of()
    private var replaceableTextures: ImmutableSet<Identifier> = ImmutableSet.of()
    private val replacementCache = mutableMapOf<Identifier, Identifier>()
    var renderingScreen = false
    val renderedTextures: Set<Identifier>
        get() = replacementCache.keys

    @JvmStatic
    fun replaceTexture(texture: Identifier): Identifier {
        if (!renderingScreen) return texture
        if (!InteractionManager.isInteracting) return texture
        if (texture !in replaceableTextures) return replacementCache.getOrPut(texture) { texture }

        val interaction = InteractionManager.interaction ?: return texture
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

    override fun reload(manager: ResourceManager?) {
        val filters = IFilterLoader.flatMap { it.value.get() }

        this.filters = ImmutableMap.copyOf(
            filters.groupBy { it.container }.mapValues { (_, list) -> LinkedLruCollection(list) }
        )
        this.replaceableTextures = ImmutableSet.copyOf(filters.flatMap { it.replacementTextures.keys })
    }
}
