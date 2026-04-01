package opekope2.optigui.internal.filter

import net.minecraft.resources.ResourceLocation
import opekope2.optigui.filter.IFilter
import opekope2.optigui.interaction.Interaction

internal class ContainerMapFilter(private val filters: Map<ResourceLocation?, ContainerMapFirstMatchFilter>) :
    IFilter<Interaction, ResourceLocation>, Iterable<IFilter<Interaction, ResourceLocation>> {
    override fun evaluate(input: Interaction): ResourceLocation? {
        return filters[input.container]?.evaluate(input) ?: filters[null]?.evaluate(input)
    }

    override fun iterator(): Iterator<IFilter<Interaction, ResourceLocation>> = filters.values.iterator()

    override fun toString(): String = javaClass.name
}
