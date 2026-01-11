package opekope2.optigui.filter

import com.google.common.collect.Multimap
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener
import opekope2.optigui.filter.texture_changer.TextureChangerFilter
import opekope2.optigui.interaction.InteractionTarget
import opekope2.optigui.util.EventCollectorLogger
import opekope2.optigui.util.LOG_KEY_RESOURCE
import opekope2.optigui.util.LOG_KEY_RESOURCE_PACK
import opekope2.optigui.util.ResourceLoadingLoggingEvent
import opekope2.optigui.util.registry.RegistryBase
import org.slf4j.event.LoggingEvent
import org.slf4j.spi.LoggingEventBuilder

/**
 * A filter supplier that loads [filters][TextureChangerFilter] from resources.
 */
interface IFilterLoader : PreparableReloadListener {
    /**
     * Gets the log entries logged during [IFilterLoader.reload].
     *
     * @see EventCollectorLogger.events
     * @see LoggingEventBuilder.addKeyValue
     * @see LOG_KEY_RESOURCE
     * @see LOG_KEY_RESOURCE_PACK
     */
    val log: List<LoggingEvent>

    /**
     * Gets the filters loaded in [IFilterLoader.reload].
     */
    val filters: Multimap<InteractionTarget, TextureChangerFilter>

    /**
     * Filter supplier registry.
     */
    companion object Registry : RegistryBase<ResourceLocation, IFilterLoader>() {
        @JvmStatic
        val lastResourceReloadLog: List<ResourceLoadingLoggingEvent>
            get() = asSequence().flatMap { it.value.log }.map(::ResourceLoadingLoggingEvent).toList()
    }
}
