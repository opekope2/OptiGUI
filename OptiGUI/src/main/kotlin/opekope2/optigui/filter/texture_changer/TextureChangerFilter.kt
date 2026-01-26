package opekope2.optigui.filter.texture_changer

import net.minecraft.resources.ResourceLocation
import opekope2.optigui.filter.ConditionalFilter
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.filter.text_style_changer.TextStyleChanger
import opekope2.optigui.util.MOD_ID

/**
 * An NBT filter specifying which GUI textures it can change to which other textures.
 *
 * @param resourceId The resource ID this filter is loaded from
 * @param filter The filter, which decides whether to change the GUI textures of the inventory GUI
 * @param textureChangers A map containing a function for each original texture, which maps it to the changed texture
 * @param spriteChangers A map containing a function for each original sprite, which maps it to the changed sprite
 * @param textStyleChangers A collection of text style changers
 */
data class TextureChangerFilter(
    val resourceId: ResourceLocation,
    private val filter: INbtFilter,
    val textureChangers: Map<ResourceLocation, ITextureChanger>,
    val spriteChangers: Map<ResourceLocation, ITextureChanger>,
    val textStyleChangers: List<TextStyleChanger>,
) : INbtFilter by filter {
    companion object {
        @JvmField
        val NO_OP = TextureChangerFilter(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, ""),
            ConditionalFilter.NEVER,
            emptyMap(),
            emptyMap(),
            emptyList(),
        )
    }
}
