package opekope2.optigui.internal

import net.minecraft.nbt.NbtElement
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.util.Identifier
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.filter.TextureChangerFilter
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.util.LinkedMruCollection

internal typealias ContainerId2FiltersMap = Map<Identifier, LinkedMruCollection<TextureChangerFilter, NbtElement>>

internal object TextureChanger : SynchronousResourceReloader {
    private var filters: ContainerId2FiltersMap = mapOf()
    private var textureChanges: Map<Identifier, Identifier> = mapOf()
    var renderingScreen = false
    val renderedTextures = mutableSetOf<Identifier>()
    var renderedCustomTextures = false
        private set

    @JvmStatic
    fun changeTexture(texture: Identifier): Identifier {
        if (!renderingScreen) return texture
        if (!InteractionManager.isInteracting) return texture
        renderedTextures += texture

        if (texture !in textureChanges) return texture
        renderedCustomTextures = true
        return textureChanges[texture]!!
    }

    fun clearCache() {
        textureChanges = InteractionManager.interaction?.let {
            filters[it.data.id]?.promoteFirstOrNull(it.createNbt())?.textureChanges
        } ?: mapOf()
        renderedTextures.clear()
        renderedCustomTextures = false
    }

    override fun reload(manager: ResourceManager?) {
        filters = IFilterLoader.flatMap { it.value.get() }.groupBy { it.container }
            .mapValues { (_, list) -> LinkedMruCollection(list) }
    }
}
