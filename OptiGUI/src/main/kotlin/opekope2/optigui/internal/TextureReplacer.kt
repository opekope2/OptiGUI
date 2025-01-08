package opekope2.optigui.internal

import net.minecraft.nbt.NbtElement
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.util.Identifier
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.filter.TextureReplacerFilter
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.util.LinkedLruCollection

internal typealias ContainerId2FiltersMap = Map<Identifier, LinkedLruCollection<TextureReplacerFilter, NbtElement>>

internal object TextureReplacer : SynchronousResourceReloader {
    private var filters: ContainerId2FiltersMap = mapOf()
    private var replaceableTextures: Set<Identifier> = setOf()
    private var replacementTextures: Map<Identifier, Identifier> = mapOf()
    var renderingScreen = false
    val renderedTextures = mutableSetOf<Identifier>()

    @JvmStatic
    fun replaceTexture(texture: Identifier): Identifier {
        if (!renderingScreen) return texture
        if (!InteractionManager.isInteracting) return texture
        if (texture !in replaceableTextures) return texture

        renderedTextures += texture

        return replacementTextures[texture] ?: texture
    }

    fun clearCache() {
        replacementTextures = InteractionManager.interaction?.let {
            filters[it.data.id]?.promoteFirstOrNull(it.createNbt())?.replacementTextures
        } ?: mapOf()
        renderedTextures.clear()
    }

    override fun reload(manager: ResourceManager?) {
        val filters = IFilterLoader.flatMap { it.value.get() }

        this.filters = filters.groupBy { it.container }.mapValues { (_, list) -> LinkedLruCollection(list) }
        this.replaceableTextures = filters.flatMapTo(HashSet(filters.size)) { it.replacementTextures.keys }
    }
}
