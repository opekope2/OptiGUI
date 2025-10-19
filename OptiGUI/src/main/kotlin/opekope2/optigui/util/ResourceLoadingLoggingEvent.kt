package opekope2.optigui.util

import net.minecraft.util.Identifier
import org.slf4j.event.Level
import org.slf4j.event.LoggingEvent
import org.slf4j.helpers.MessageFormatter
import java.util.function.Predicate

/**
 * A logging event, which contains information about the resource pack and resource where it occurred, and has a total
 * ordering.
 *
 * @param message The log message with parameters inlined
 * @param level The log entry severity
 * @param packName The resource pack name where this log entry occurred
 * @param resourceId The resource ID where this log entry occurred
 */
data class ResourceLoadingLoggingEvent(
    val message: String,
    val level: Level,
    val packName: String?,
    val resourceId: Identifier?
) : Comparable<ResourceLoadingLoggingEvent> {
    /**
     * Creates a new [ResourceLoadingLoggingEvent] from a [LoggingEvent].
     *
     * @param event The logging event to extract information from
     * @param packExists A predicate testing if a resource pack with a name exists
     */
    constructor(event: LoggingEvent, packExists: Predicate<String>) : this(
        MessageFormatter.basicArrayFormat(event.message, event.argumentArray),
        event.level,
        event.keyValuePairs?.firstOrNull { it.key == LOG_KEY_RESOURCE_PACK && it.value is String && packExists.test(it.value as String) }?.value as? String,
        event.keyValuePairs?.firstOrNull { it.key == LOG_KEY_RESOURCE && it.value is Identifier }?.value as? Identifier
    )

    // Unknown pack is last
    private fun comparePackName(second: ResourceLoadingLoggingEvent): Int {
        val pack1 = packName ?: return if (second.packName == null) 0 else 1
        val pack2 = second.packName ?: return -1
        return pack1.compareTo(pack2)
    }

    // Unknown resource is last
    private fun compareResourceId(second: ResourceLoadingLoggingEvent): Int {
        val res1 = resourceId ?: return if (second.resourceId == null) 0 else 1
        val res2 = second.resourceId ?: return -1
        return res1.compareTo(res2)
    }

    override fun compareTo(other: ResourceLoadingLoggingEvent) = when (val result = comparePackName(other)) {
        0 -> compareResourceId(other)
        else -> result
    }
}
