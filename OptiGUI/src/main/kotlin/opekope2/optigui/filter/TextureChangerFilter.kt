package opekope2.optigui.filter

import net.minecraft.util.Identifier

/**
 * An [INbtFilter] specifying which GUI textures it can change to which other textures.
 *
 * @param container The container to change GUI textures of
 * @param filter The filter, which decides whether to change the GUI textures of the container
 * @param textureChanges A map containing the original and the changed textures
 */
class TextureChangerFilter(
    val container: Identifier,
    filter: INbtFilter,
    textureChanges: Map<Identifier, Identifier>
) : INbtFilter by filter {
    /**
     * A map containing the original and the changed textures.
     */
    val textureChanges = textureChanges.toMap()
}
