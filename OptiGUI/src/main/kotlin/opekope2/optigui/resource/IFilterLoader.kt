package opekope2.optigui.resource

import net.minecraft.resource.ResourceManager
import org.slf4j.Logger

/**
 * A resource loader for loading filters from resource packs.
 */
interface IFilterLoader {
    /**
     * Loads raw filter data from supported resources using the given resource manager.
     *
     * @param resourceManager The resource manager to load filters from
     * @param logger The logger, which can be used for logging, and can provide insights about resource loading errors
     */
    fun loadRawFilters(resourceManager: ResourceManager, logger: Logger): Collection<IRawFilterData>
}
