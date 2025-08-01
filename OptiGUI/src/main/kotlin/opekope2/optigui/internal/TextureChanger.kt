package opekope2.optigui.internal

import net.minecraft.nbt.NbtElement
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.util.Identifier
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.filter.texture_changer.TextureChangerFilter
import opekope2.optigui.interaction.InteractionManager
import opekope2.optigui.util.LinkedMruCollection

internal object TextureChanger : SynchronousResourceReloader {
    var filter: TextureChangerFilter = TextureChangerFilter.NO_OP
        private set
    private var filters = mapOf<Identifier, LinkedMruCollection<TextureChangerFilter, NbtElement>>()
    var renderingScreen = false
    val renderedTextures = mutableSetOf<Identifier>()
    val renderedSprites = mutableSetOf<Identifier>()
    var renderedCustomTextures = false
        private set

    @JvmStatic
    fun changeTexture(texture: Identifier): Identifier {
        if (!renderingScreen) return texture
        if (!InteractionManager.isInteracting) return texture
        renderedTextures += texture

        if (texture !in filter.textureChangers) return texture
        renderedCustomTextures = true
        return filter.textureChangers.getValue(texture).apply(texture)
    }

    @JvmStatic
    fun changeSprite(sprite: Identifier): Identifier {
        if (!renderingScreen) return sprite
        if (!InteractionManager.isInteracting) return sprite
        renderedSprites += sprite

        if (sprite !in filter.spriteChangers) return sprite
        renderedCustomTextures = true
        return filter.spriteChangers.getValue(sprite).apply(sprite)
    }

    fun clearCache() {
        filter = InteractionManager.interaction?.let {
            filters[it.data.id]?.promoteFirstOrNull(it.createNbt())
        } ?: TextureChangerFilter.NO_OP
        renderedTextures.clear()
        renderedCustomTextures = false
    }

    override fun reload(manager: ResourceManager?) {
        filters = IFilterLoader.Registry.flatMap { it.value.get() }.groupBy { it.inventoryId }
            .mapValues { (_, list) -> LinkedMruCollection(list) }
    }
}
