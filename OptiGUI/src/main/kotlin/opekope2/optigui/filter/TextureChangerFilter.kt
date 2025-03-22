package opekope2.optigui.filter

import net.minecraft.util.Identifier

/**
 * An [INbtFilter] specifying which GUI textures it can change to which other textures.
 *
 * @param inventoryId The block, entity, or item to change the inventory GUI textures of
 * @param resourceId The resource ID this filter is loaded from
 * @param filter The filter, which decides whether to change the GUI textures of the inventory GUI
 * @param textureChanges A map containing the original and the changed textures
 * @param spriteChanges A map containing the original and the changed sprites
 */
data class TextureChangerFilter(
    val inventoryId: Identifier,
    val resourceId: Identifier,
    private val filter: INbtFilter,
    val textureChanges: Map<Identifier, Identifier>,
    val spriteChanges: Map<Identifier, Identifier>
) : INbtFilter by filter
