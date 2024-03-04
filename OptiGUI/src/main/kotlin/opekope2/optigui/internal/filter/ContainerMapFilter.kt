package opekope2.optigui.internal.filter

import net.minecraft.util.Identifier
import opekope2.optigui.filter.IFilter
import opekope2.optigui.filter.IFilter.Result.Match
import opekope2.optigui.filter.IFilter.Result.Mismatch
import opekope2.optigui.interaction.Interaction

internal class ContainerMapFilter(private val filters: Map<Identifier?, ContainerMapFirstMatchFilter>) :
    IFilter<Interaction, Identifier>, Iterable<IFilter<Interaction, Identifier>> {
    override fun evaluate(value: Interaction): IFilter.Result<out Identifier> {
        val result = filters[value.container]?.evaluate(value)
        return if (result is Match) result
        else filters[null]?.evaluate(value) ?: Mismatch
    }

    override fun iterator(): Iterator<IFilter<Interaction, Identifier>> = filters.values.iterator()

    override fun toString(): String = javaClass.name
}
