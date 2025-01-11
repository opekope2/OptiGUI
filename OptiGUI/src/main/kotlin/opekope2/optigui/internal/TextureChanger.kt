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
    private var changeableTextures: Set<Identifier> = setOf()
    private var textureChanges: Map<Identifier, Identifier> = mapOf()
    var renderingScreen = false
    val renderedTextures = mutableSetOf<Identifier>()

    @JvmStatic
    fun changeTexture(texture: Identifier): Identifier {
        if (!renderingScreen) return texture
        if (!InteractionManager.isInteracting) return texture
        renderedTextures += texture

        return if (texture !in changeableTextures) texture
        else textureChanges[texture] ?: texture
    }

    fun clearCache() {
        textureChanges = InteractionManager.interaction?.let {
            filters[it.data.id]?.promoteFirstOrNull(it.createNbt())?.textureChanges
        } ?: mapOf()
        renderedTextures.clear()
    }

    override fun reload(manager: ResourceManager?) {
        val filters = IFilterLoader.flatMap { it.value.get() }

        this.filters = filters.groupBy { it.container }.mapValues { (_, list) -> LinkedMruCollection(list) }
        this.changeableTextures = filters.flatMapTo(HashSet(filters.size)) { it.textureChanges.keys }
    }
}
