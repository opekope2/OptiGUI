package opekope2.optigui.filter.factory

import net.minecraft.util.Identifier
import opekope2.optigui.filter.IFilter
import opekope2.optigui.interaction.Interaction

/**
 * A filter descriptor, which specifies every [Interaction.texture] the provided [filter] can replace.
 *
 * @param filter The filter, which does the replacing
 * @param replaceableTextures The textures [filter] can replace
 */
data class FilterFactoryResult(
    val filter: IFilter<Interaction, Identifier>,
    val replaceableTextures: Set<Identifier>
)
