package opekope2.optigui.filter.texture_changer

import net.minecraft.util.Identifier
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.util.MOD_ID

/**
 * An [INbtFilter] specifying which GUI textures it can change to which other textures.
 *
 * @param inventoryId The block, entity, or item to change the inventory GUI textures of
 * @param resourceId The resource ID this filter is loaded from
 * @param filter The filter, which decides whether to change the GUI textures of the inventory GUI
 * @param textureChangers A map containing a function for each original texture, which maps it to the changed texture
 * @param spriteChangers A map containing a function for each original sprite, which maps it to the changed sprite
 */
data class TextureChangerFilter(
    val inventoryId: Identifier,
    val resourceId: Identifier,
    private val filter: INbtFilter,
    val textureChangers: Map<Identifier, ITextureChanger>,
    val spriteChangers: Map<Identifier, ITextureChanger>
) : INbtFilter by filter {
    companion object {
        @JvmField
        val NO_OP = TextureChangerFilter(
            Identifier.of(MOD_ID, ""),
            Identifier.of(MOD_ID, ""),
            INbtFilter.NEVER_MATCH,
            mapOf(),
            mapOf()
        )
    }
}
