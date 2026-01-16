package opekope2.optigui.config

import opekope2.optigui.filter.IFilterLoader
import org.slf4j.event.Level

/**
 * Resource loading problem filter.
 */
enum class ResourceLoadingLogFilter(private val minLevel: Level) {
    /**
     * Filter for no problems
     */
    NOTHING(Level.ERROR),

    /**
     * Filter for errors only
     */
    ERRORS_ONLY(Level.WARN),

    /**
     * Filter for errors and warnings
     */
    ERRORS_AND_WARNINGS(Level.INFO);

    /**
     * Returns if the resource loading log should be shown to the user.
     */
    fun shouldShowResourceLoadingLog() = IFilterLoader.any { (_, loader) -> loader.log.any { it.level < minLevel } }
}
