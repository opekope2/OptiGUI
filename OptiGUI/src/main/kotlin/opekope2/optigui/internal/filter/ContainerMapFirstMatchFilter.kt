package opekope2.optigui.internal.filter

import net.minecraft.util.Identifier
import opekope2.optigui.filter.FirstMatchFilter
import opekope2.optigui.filter.IFilter
import opekope2.optigui.interaction.Interaction

internal class ContainerMapFirstMatchFilter(
    private val container: Identifier?,
    filters: Collection<IFilter<Interaction, Identifier>>
) : FirstMatchFilter<Interaction, Identifier>(filters) {
    override fun toString() = "${javaClass.name}, container: $container"
}
