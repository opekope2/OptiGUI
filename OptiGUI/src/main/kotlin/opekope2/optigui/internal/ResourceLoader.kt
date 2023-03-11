package opekope2.optigui.internal

import net.minecraft.util.Identifier
import opekope2.filter.Filter
import opekope2.filter.FilterResult
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.filter.IdentifiableFilter
import opekope2.optigui.internal.interaction.filterFactories
import opekope2.optigui.internal.service.ResourceLoaderService
import opekope2.optigui.resource.Resource
import opekope2.util.dump

internal object ResourceLoader : ResourceLoaderService {
    override fun loadResources(resources: Iterable<Resource>) {
        val filters = mutableListOf<Filter<Interaction, Identifier>>()
        val replaceableTextures = mutableSetOf<Identifier>()

        for (resource in resources) {
            for ((modId, factories) in filterFactories) {
                for (factory in factories) {
                    val filterInfo = try {
                        factory(resource)
                    } catch (exception: Exception) {
                        logger.warn("$modId threw an exception while creating filter for ${resource.id}.", exception)
                        continue
                    } ?: continue

                    filters.add(IdentifiableFilter(modId, filterInfo))
                    replaceableTextures.addAll(filterInfo.replaceableTextures)
                }
            }
        }

        val filter = RootFilter(filters)

        TextureReplacer.filter = filter
        TextureReplacer.replaceableTextures = replaceableTextures

        logger.debug("Filter chain loaded on resource reload:\n${filter.dump()}")
    }

    private class RootFilter(private val filters: Iterable<Filter<Interaction, Identifier>>) :
        Filter<Interaction, Identifier>, Iterable<Filter<Interaction, Identifier>> {
        override fun evaluate(value: Interaction): FilterResult<out Identifier> {
            filters.forEach { filter ->
                filter.evaluate(value).also { if (it is FilterResult.Match) return it }
            }
            return FilterResult.Skip()
        }

        override fun iterator(): Iterator<Filter<Interaction, Identifier>> = filters.iterator()

        override fun toString(): String = "OptiGUI Root Filter"
    }
}
