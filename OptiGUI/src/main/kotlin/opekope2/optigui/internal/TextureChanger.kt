package opekope2.optigui.internal

import com.google.common.collect.LinkedListMultimap
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.filter.texture_changer.TextureChangerFilter
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.interaction.InteractionTarget
import opekope2.optigui.util.collections.LinkedMruCollection
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
object TextureChanger : ResourceManagerReloadListener {
    var filter: TextureChangerFilter = TextureChangerFilter.NO_OP
        private set
    var filters = mapOf<InteractionTarget, LinkedMruCollection<TextureChangerFilter>>()
        private set
    var renderingScreen = false
    val renderedTextures = mutableSetOf<ResourceLocation>()
    val renderedSprites = mutableSetOf<ResourceLocation>()
    var renderedCustomTextures = false
        private set

    @JvmStatic
    fun changeTexture(texture: ResourceLocation): ResourceLocation {
        if (!renderingScreen) return texture
        if (!InteractionManager.isInteracting) return texture
        renderedTextures += texture

        if (texture !in filter.textureChangers) return texture
        renderedCustomTextures = true
        return filter.textureChangers.getValue(texture).apply(texture)
    }

    @JvmStatic
    fun changeSprite(sprite: ResourceLocation): ResourceLocation {
        if (!renderingScreen) return sprite
        if (!InteractionManager.isInteracting) return sprite
        renderedSprites += sprite

        if (sprite !in filter.spriteChangers) return sprite
        renderedCustomTextures = true
        return filter.spriteChangers.getValue(sprite).apply(sprite)
    }

    fun clearCache() {
        filter = updateFilter()
        renderedTextures.clear()
        renderedCustomTextures = false
    }

    private fun updateFilter(): TextureChangerFilter {
        val interaction = InteractionManager.interaction ?: return TextureChangerFilter.NO_OP
        val filters = filters[interaction.target] ?: return TextureChangerFilter.NO_OP
        val nbt = interaction.createNbt()
        return filters.promoteFirstOrNull { it.test(nbt, nbt) } ?: TextureChangerFilter.NO_OP
    }

    override fun onResourceManagerReload(manager: ResourceManager) {
        val map = LinkedListMultimap.create<InteractionTarget, TextureChangerFilter>()
        for ((_, filterLoader) in IFilterLoader.Registry) {
            map.putAll(filterLoader.filters)
        }
        filters = map.asMap().mapValues { (_, list) -> LinkedMruCollection(list) }
    }
}
