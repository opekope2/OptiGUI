package opekope2.optigui.internal.lilac_resource_loader

import net.minecraft.util.Identifier
import opekope2.lilac.api.resource.IResourceAccess
import opekope2.lilac.api.resource.IResourceReader
import opekope2.lilac.api.resource.loading.IResourceLoader
import opekope2.lilac.api.resource.loading.IResourceLoadingSession
import opekope2.lilac.util.Util
import opekope2.optigui.api.IOptiGuiApi
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.lilac_resource_loading.IOptiGuiExtension
import opekope2.optigui.filter.*
import opekope2.optigui.internal.selector.selectors
import opekope2.optigui.properties.IGeneralProperties
import opekope2.util.OPTIGUI_NAMESPACE
import opekope2.util.OPTIGUI_RESOURCES_ROOT
import opekope2.util.delimiters
import opekope2.util.resolvePath
import org.ini4j.Ini

@Suppress("unused")
class OptiGuiResourceLoader(private val optigui: IOptiGuiExtension) : IResourceLoader {
    override fun getStartingPath(): String = OPTIGUI_RESOURCES_ROOT

    override fun canLoad(resourceName: String): Boolean = resourceName.endsWith(".ini")

    override fun canLoad(resourcePath: Identifier): Boolean =
        resourcePath.namespace == OPTIGUI_NAMESPACE && resourcePath.path.endsWith(".ini")

    override fun loadResource(resource: IResourceReader): Any = resource.id to resource.inputStream.use(::Ini)

    @Suppress("UNCHECKED_CAST")
    override fun processResource(resource: Any) {
        val (resId, ini) = resource as Pair<Identifier, Ini>
        processIni(resId, ini)
    }

    private fun processIni(resource: Identifier, ini: Ini) {
        for ((sectionName, section) in ini) {
            val replacement = (section["replacement"].also {
                if (it == null) optigui.warn(
                    resource,
                    "[$sectionName] Missing `replacement`"
                )
            } ?: continue).let { replace -> resolvePath(replace, resource) }.also {
                if (it == null) optigui.warn(
                    resource,
                    "[$sectionName] Failed to parse `replacement=${section["replacement"]}`: Resource path cannot be resolved"
                )
            } ?: continue
            if (!resourceAccess.getResource(replacement).exists()) {
                optigui.warn(
                    resource,
                    "[$sectionName]: Failed to parse `replacement=${section["replacement"]}`: Texture doesn't exist"
                )
                continue
            }

            val containers =
                sectionName.split(*delimiters).filter { !it.startsWith('#') }.mapNotNull(Identifier::tryParse)

            val sectionFilters = section.mapNotNull { (key, value) ->
                try {
                    selectors[key]?.createFilter(value)
                } catch (e: Exception) {
                    optigui.warn(resource, "Failed to parse selector `$key=$value`: ${e.message ?: e}")
                    null
                }
            }

            for (container in containers) {
                val replaceableTextures = mutableSetOf(
                    section["interaction.texture"]?.let(Identifier::tryParse)
                        ?: optiguiApi.getContainerTexture(container)
                        ?: continue
                )
                // Because Mojang
                if (minecraft_1_19_4 && container == smithingTable) {
                    replaceableTextures += Identifier("textures/gui/container/legacy_smithing.png")
                }

                val filters = mutableListOf<Filter<Interaction, *>>(
                    PreProcessorFilter.nullGuarded(
                        { (it.data as? IGeneralProperties)?.container },
                        FilterResult.mismatch(),
                        EqualityFilter(container)
                    ),
                    PreProcessorFilter.nullGuarded(
                        { it.texture },
                        FilterResult.mismatch(),
                        ContainingFilter(replaceableTextures)
                    ),
                )
                filters.addAll(sectionFilters)

                optigui.addFilter(
                    resource,
                    PostProcessorFilter(ConjunctionFilter(filters), replacement),
                    replaceableTextures,
                    section["load.priority"]?.toIntOrNull() ?: 0
                )
            }
        }
    }

    override fun close() {
        optigui.close()
    }

    companion object Factory : IResourceLoader.IFactory {
        private val optiguiApi = IOptiGuiApi.getImplementation()
        private val resourceAccess = IResourceAccess.getInstance()
        private val minecraft_1_19_4 = Util.checkModVersion("minecraft") { v -> "1.19.4" in v.friendlyString }
        private val smithingTable = Identifier("smithing_table")

        override fun createResourceLoader(session: IResourceLoadingSession): IResourceLoader =
            OptiGuiResourceLoader(session["optigui"] as IOptiGuiExtension)
    }
}
