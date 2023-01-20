package opekope2.optigui.internal

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.filter.Filter
import opekope2.filter.FilterResult
import opekope2.filter.filterFactories
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.provider.IResourceManagerProvider
import opekope2.optigui.provider.getProvider
import opekope2.optigui.resource.Resource

internal object ResourceLoader : SimpleSynchronousResourceReloadListener {
    private val resourceManagerProvider = getProvider<IResourceManagerProvider>()

    override fun getFabricId() = Identifier("optigui", "optigui_resource_loader")

    override fun reload(manager: ResourceManager) {
        val wrapper = resourceManagerProvider.wrapResourceManager(manager)
        val filters = mutableListOf<Filter<Interaction, Identifier>>()
        val replaceableTextures = mutableSetOf<Identifier>()

        for (resource in wrapper.findResources("optifine/gui/container") { it.toString().endsWith(".properties") }) {
            filterFactories.forEach {
                it(Resource(wrapper, resource))?.let {
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
