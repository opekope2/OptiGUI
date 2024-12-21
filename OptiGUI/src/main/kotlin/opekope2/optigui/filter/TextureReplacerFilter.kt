package opekope2.optigui.filter

import com.google.common.collect.ImmutableMap
import net.minecraft.util.Identifier

/**
 * An [INbtFilter] specifying which textures can it replace with which textures.
 *
 * @param container The container to replace textures of
 * @param filter The filter deciding whether to replace the textures of the container
 * @param replacementTextures A map containing the original and the replaced textures
 */
class TextureReplacerFilter(
    val container: Identifier,
    filter: INbtFilter,
    val replacementTextures: ImmutableMap<Identifier, Identifier>
) : INbtFilter by filter
