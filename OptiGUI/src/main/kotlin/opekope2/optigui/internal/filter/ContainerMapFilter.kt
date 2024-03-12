package opekope2.optigui.internal.filter

import net.minecraft.util.Identifier
import opekope2.optigui.filter.IFilter
import opekope2.optigui.interaction.Interaction

internal class ContainerMapFilter(private val filters: Map<Identifier?, ContainerMapFirstMatchFilter>) :
    IFilter<Interaction, Identifier>, Iterable<IFilter<Interaction, Identifier>> {
    override fun evaluate(input: Interaction): Identifier? {
        return filters[input.container]?.evaluate(input) ?: filters[null]?.evaluate(input)
    }

    override fun iterator(): Iterator<IFilter<Interaction, Identifier>> = filters.values.iterator()

    override fun toString(): String = javaClass.name
}
