package opekope2.optigui.internal

import net.minecraft.util.Identifier
import opekope2.filter.Filter
import opekope2.filter.FilterResult
import opekope2.filter.filterFactories
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.resource.Resource

object ResourceLoader {
    fun loadResources(resources: Iterable<Resource>) {
        val filters = mutableListOf<Filter<Interaction, Identifier>>()
        val replaceableTextures = mutableSetOf<Identifier>()

        for (resource in resources) {
            filterFactories.forEach {
                it(resource)?.let {
                    filters.add(it.filter)
                    replaceableTextures.addAll(it.replaceableTextures)
                }
            }
        }

        InteractionHandler.filter = CascadeFilter(filters)
        InteractionHandler.replaceableTextures = replaceableTextures
    }

    private class CascadeFilter(private val filters: Iterable<Filter<Interaction, Identifier>>) :
        Filter<Interaction, Identifier> {
        override fun evaluate(value: Interaction): FilterResult<out Identifier> {
            filters.forEach { filter ->
                filter.evaluate(value).let { if (it is FilterResult.Match) return it }
            }
            return FilterResult.Skip()
        }
    }
}
