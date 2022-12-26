package opekope2.util

import opekope2.filter.FilterResult

fun <T> catchAll(function: () -> T): T? = try {
    function()
} catch (_: Throwable) {
    null
}

/**
 * Creates a new filter result, which has the same [FilterResult.skip] and [FilterResult.match] as the current one,
 * optionally converting the [FilterResult.replacement].
 *
 * @param TSource The type of the filter result to convert from
 * @param TDest The type of the filter result to convert to
 * @param replacementConverter The converter function to convert the replacement.
 * If it is null, the new [FilterResult.replacement] will be null as well
 */
@JvmOverloads
fun <TSource, TDest> FilterResult<TSource>.convert(replacementConverter: ((TSource) -> TDest)? = null): FilterResult<out TDest> =
    FilterResult(skip, match, replacement?.let { replacementConverter?.invoke(it) })
