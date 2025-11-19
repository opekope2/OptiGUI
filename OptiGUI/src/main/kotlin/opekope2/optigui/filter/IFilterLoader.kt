package opekope2.optigui.filter

import com.google.common.collect.Multimap
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener
import opekope2.optigui.filter.texture_changer.TextureChangerFilter
import opekope2.optigui.interaction.InteractionTarget
import opekope2.optigui.registry.RegistryBase
import opekope2.optigui.util.EventCollectorLogger
import opekope2.optigui.util.ResourceLoadingLoggingEvent

/**
 * A filter supplier that loads [filters][TextureChangerFilter] from resources.
 */
interface IFilterLoader : PreparableReloadListener {
    /**
     * Gets the warnings and errors occurred while loading filters in [IFilterLoader.reload].
     *
     * @see [EventCollectorLogger.events]
     */
    val errors: List<ResourceLoadingLoggingEvent>

    /**
     * Gets the filters loaded in [IFilterLoader.reload].
     */
    val filters: Multimap<InteractionTarget, TextureChangerFilter>

    /**
     * Filter supplier registry.
     */
    companion object Registry : RegistryBase<ResourceLocation, IFilterLoader>()
}
