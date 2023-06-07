package opekope2.optigui.internal

import net.minecraft.util.Identifier
import opekope2.filter.Filter
import opekope2.filter.FirstMatchFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.filter.IdentifiableFilter
import opekope2.optigui.internal.filter.factory.ReusableFilterFactoryContext
import opekope2.optigui.internal.interaction.FilterFactoryStore.filterFactories
import opekope2.optigui.internal.service.ResourceLoaderService
import opekope2.optigui.resource.OptiGuiResource
import opekope2.util.dump

internal object ResourceLoader : ResourceLoaderService {
    override fun loadResources(resources: Set<OptiGuiResource>) {
        val filters = mutableListOf<Filter<Interaction, Identifier>>()
        val replaceableTextures = mutableSetOf<Identifier>()

        val context = ReusableFilterFactoryContext()
        for (resource in resources) {
            logger.info("Loading ${resource.id} from ${resource.resourcePack}")

            context.resource = resource

            for ((modId, factories) in filterFactories) {
                context.modId = modId

                for (factory in factories) {
                    val (filter, replaceable) = try {
                        factory.createFilter(context)
                    } catch (exception: Exception) {
                        logger.warn("$modId threw an exception while creating filter for ${resource.id}.", exception)
                        continue
                    } ?: continue

                    filters.add(IdentifiableFilter(modId, filter, replaceable, resource))
                    replaceableTextures.addAll(replaceable)
                }
            }
        }

        val filter = FirstMatchFilter(filters)

        TextureReplacer.filter = filter
        TextureReplacer.replaceableTextures = replaceableTextures

        logger.debug("Filter chain loaded on resource reload:\n${filter.dump()}")
    }
}
