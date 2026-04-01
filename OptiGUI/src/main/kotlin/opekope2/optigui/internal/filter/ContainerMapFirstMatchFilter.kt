package opekope2.optigui.internal.filter

import net.minecraft.resources.ResourceLocation
import opekope2.optigui.filter.FirstMatchFilter
import opekope2.optigui.filter.IFilter
import opekope2.optigui.interaction.Interaction

internal class ContainerMapFirstMatchFilter(
    private val container: ResourceLocation?,
    filters: Collection<IFilter<Interaction, ResourceLocation>>
) : FirstMatchFilter<Interaction, ResourceLocation>(filters) {
    override fun toString() = "${javaClass.name}, container: $container"
}
