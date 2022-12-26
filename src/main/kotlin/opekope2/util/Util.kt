package opekope2.util

import opekope2.optigui.filter.FilterResult

fun <T> catchAll(function: () -> T): T? = try {
    function()
} catch (_: Throwable) {
    null
}

/**
 * Creates a new filter result, which has the same [FilterResult.skip] and [FilterResult.match] as the current one,
 * optionally converting the [FilterResult.replacement]
 *
 * @param replacementConverter The converter function to convert the replacement.
 * If it is null, the new [FilterResult.replacement] will be null as well
 * @param U the replacement type of the filter result to convert to
 */
@JvmOverloads
fun <T, U> FilterResult<T>.convert(replacementConverter: ((T) -> U)? = null): FilterResult<out U> =
    if (skip) FilterResult.skip() else FilterResult.create(match, replacement?.let { replacementConverter?.invoke(it) })
