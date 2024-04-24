package opekope2.optigui.internal.util

import net.minecraft.util.Identifier
import opekope2.optigui.util.LOG_KEY_CONTAINER
import opekope2.optigui.util.LOG_KEY_RESOURCE
import org.slf4j.Logger
import org.slf4j.event.Level
import java.io.StringWriter

internal fun String?.toBoolean(): Boolean? {
    return (this ?: return null).lowercase().toBooleanStrictOrNull()
}

internal fun String.trimParentheses() = trimStart('(').trimEnd(')')

internal val delimiters = charArrayOf(' ', '\t')

internal fun CharSequence.splitIgnoreEmpty(vararg delimiters: Char) =
    this.split(*delimiters).filter { it.isNotEmpty() }.ifEmpty { null }

internal inline fun buildString(builderAction: StringWriter.() -> Unit): String {
    return StringWriter().apply(builderAction).toString()
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <T> Collection<T>.assertNotEmpty(): Collection<T> {
    assert(isNotEmpty())
    return this
}

internal inline fun <T, TResult> Collection<T>.mapNotNull(
    transform: (T) -> TResult?,
    whenNotTransformed: (Collection<T>) -> Nothing
): Collection<TResult> {
    val result = mutableListOf<TResult>()
    val notTransformed = mutableListOf<T>()

    for (item in this) {
        val transformed = transform(item)
        if (transformed != null) result += transformed
        else notTransformed += item
    }

    if (notTransformed.isNotEmpty()) {
        whenNotTransformed(notTransformed)
    }

    return result
}

internal fun joinNotFound(strings: Collection<String>) = strings.joinToString { "`$it`" }

internal fun Logger.eventBuilder(level: Level, resource: Identifier, container: String?) =
    atLevel(level).addKeyValue(LOG_KEY_RESOURCE, resource).addKeyValue(LOG_KEY_CONTAINER, container)

internal fun Logger.eventBuilder(level: Level, resource: Identifier, container: Identifier?) =
    eventBuilder(level, resource, container?.toString())
