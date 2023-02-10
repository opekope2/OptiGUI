package opekope2.optigui.internal

import net.minecraft.util.Identifier
import opekope2.filter.Filter
import opekope2.filter.FilterResult
import opekope2.filter.filterFactories
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.logger
import opekope2.optigui.resource.Resource
import opekope2.util.dump

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

        val filter = RootFilter(filters)

        InteractionHandler.filter = filter
        InteractionHandler.replaceableTextures = replaceableTextures

        logger.info("Filter chain loaded on resource reload:\n${filter.dump()}")
    }

    private class RootFilter(private val filters: Iterable<Filter<Interaction, Identifier>>) :
        Filter<Interaction, Identifier>, Iterable<Filter<Interaction, Identifier>> {
        override fun evaluate(value: Interaction): FilterResult<out Identifier> {
            filters.forEach { filter ->
                filter.evaluate(value).let { if (it is FilterResult.Match) return it }
            }
            return FilterResult.Skip()
        }

        override fun iterator(): Iterator<Filter<Interaction, Identifier>> = filters.iterator()

        override fun toString(): String = "OptiGUI Root Filter"
    }
}
