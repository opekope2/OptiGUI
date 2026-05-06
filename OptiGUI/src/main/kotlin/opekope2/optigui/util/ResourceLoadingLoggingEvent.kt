package opekope2.optigui.util

import net.minecraft.resources.ResourceLocation
import org.slf4j.event.Level
import org.slf4j.event.LoggingEvent
import org.slf4j.helpers.MessageFormatter

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
    val resourceId: ResourceLocation?
) : Comparable<ResourceLoadingLoggingEvent> {
    constructor(event: LoggingEvent) : this(
        MessageFormatter.basicArrayFormat(event.message, event.argumentArray),
        event.level,
        event.findValue<String>(LOG_KEY_RESOURCE_PACK, mc.resourcePackRepository::isAvailable),
        event.findValue<ResourceLocation>(LOG_KEY_RESOURCE) { true }
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

    private companion object {
        private inline fun <reified T> LoggingEvent.findValue(key: String, valuePredicate: (T) -> Boolean): T? {
            val pair = keyValuePairs
                ?.firstOrNull { pair -> pair.key == key && pair.value.let { it is T && valuePredicate(it) } }
                ?: return null
            return pair.value as T
        }
    }
}
