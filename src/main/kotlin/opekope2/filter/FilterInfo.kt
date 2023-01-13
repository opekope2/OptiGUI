package opekope2.filter

import net.minecraft.util.Identifier
import opekope2.optigui.interaction.Interaction

/**
 * A filter descriptor, which specifies every [Interaction.texture] the provided [filter] can replace.
 *
 * @param filter The filter, which does the replacing
 * @param replaceableTextures The textures [filter] can replace
 */
class FilterInfo(
    val filter: Filter<Interaction, Identifier>,
    val replaceableTextures: Set<Identifier>
)
