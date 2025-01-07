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

internal typealias ContainerId2FiltersMap = ImmutableMap<Identifier, LinkedLruCollection<TextureReplacerFilter, NbtElement>>

internal object TextureReplacer : SynchronousResourceReloader {
    private var filters: ContainerId2FiltersMap = ImmutableMap.of()
    private var replaceableTextures: ImmutableSet<Identifier> = ImmutableSet.of()
    private var replacements: ImmutableMap<Identifier, Identifier> = ImmutableMap.of()
    var renderingScreen = false
    val renderedTextures = mutableSetOf<Identifier>()

    @JvmStatic
    fun replaceTexture(texture: Identifier): Identifier {
        if (!renderingScreen) return texture
        if (!InteractionManager.isInteracting) return texture
        if (texture !in replaceableTextures) return texture

        renderedTextures += texture

        return replacements[texture] ?: texture
    }

    fun clearCache() {
        replacements = InteractionManager.interaction?.let {
            filters[it.data.id]?.promoteFirstOrNull(it.createNbt())?.replacementTextures
        } ?: ImmutableMap.of()
        renderedTextures.clear()
    }

    override fun reload(manager: ResourceManager?) {
        val filters = IFilterLoader.flatMap { it.value.get() }

        this.filters = ImmutableMap.copyOf(
            filters.groupBy { it.container }.mapValues { (_, list) -> LinkedLruCollection(list) }
        )
        this.replaceableTextures = ImmutableSet.copyOf(filters.flatMap { it.replacementTextures.keys })
    }
}
