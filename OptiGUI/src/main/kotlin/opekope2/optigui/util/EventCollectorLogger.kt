package opekope2.optigui.util

import org.slf4j.Logger
import org.slf4j.Marker
import org.slf4j.event.Level
import org.slf4j.event.LoggingEvent
import org.slf4j.helpers.AbstractLogger
import org.slf4j.spi.LoggingEventAware
import org.slf4j.spi.LoggingEventBuilder

/**
 * A logger implementation, which delegates logging to [delegate] and collects all logging events to [events].
 */
class EventCollectorLogger(private val delegate: Logger) : AbstractLogger(), LoggingEventAware {
    /**
     * The collected logging events.
     */
    val events = mutableListOf<LoggingEvent>()

    override fun isTraceEnabled() = delegate.isTraceEnabled

    override fun isTraceEnabled(marker: Marker) = delegate.isTraceEnabled(marker)

    override fun isDebugEnabled() = delegate.isDebugEnabled

    override fun isDebugEnabled(marker: Marker) = delegate.isDebugEnabled(marker)

    override fun isInfoEnabled() = delegate.isInfoEnabled

    override fun isInfoEnabled(marker: Marker) = delegate.isInfoEnabled(marker)

    override fun isWarnEnabled() = delegate.isWarnEnabled

    override fun isWarnEnabled(marker: Marker) = delegate.isWarnEnabled(marker)

    override fun isErrorEnabled() = delegate.isErrorEnabled

    override fun isErrorEnabled(marker: Marker) = delegate.isErrorEnabled(marker)

    override fun getFullyQualifiedCallerName(): String? = null

    override fun handleNormalizedLoggingCall(
        level: Level?,
        marker: Marker?,
        messagePattern: String?,
        arguments: Array<out Any>?,
        throwable: Throwable?
    ) {
        this.atLevel(level)
            .addMarker(marker)
            .setCause(throwable)
            .log(messagePattern, *(arguments ?: arrayOf()))
    }

    override fun log(event: LoggingEvent) {
        events += event

        if (delegate is LoggingEventAware) {
            delegate.log(event)
            return
        }

        var builder = delegate.atLevel(event.level).setCause(event.throwable)
        if (event.markers != null) builder = event.markers.fold(builder, LoggingEventBuilder::addMarker)
        if (event.keyValuePairs != null)
            builder = event.keyValuePairs.fold(builder) { builder, pair -> builder.addKeyValue(pair.key, pair.value) }

        builder.log(event.message, *(event.argumentArray ?: arrayOf()))
    }
}
