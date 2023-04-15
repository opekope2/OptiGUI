package opekope2.util

import opekope2.filter.FilterResult
import java.time.LocalDateTime
import java.time.Month

/**
 * Converts the given string to a boolean:
 * - `true`, if the string is "true" (case-insensitive)
 * - `false`, if the string is "false" (case-insensitive)
 * - `null` otherwise
 */
internal fun String?.toBoolean(): Boolean? {
    return (this ?: return null).lowercase().toBooleanStrictOrNull()
}

/**
 * If the current [FilterResult] is [FilterResult.Match], change its result to the given one.
 * Otherwise, return the original.
 *
 * @param TOld The type of the old result
 * @param TNew The type of the new result
 * @param result The new result
 */
fun <TOld, TNew> FilterResult<TOld>.withResult(result: TNew): FilterResult<TNew> = when (this) {
    is FilterResult.Skip -> FilterResult.Skip()
    is FilterResult.Mismatch -> FilterResult.Mismatch()
    is FilterResult.Match -> FilterResult.Match(result)
}

/**
 * Trim parentheses from the start and end of a string.
 */
fun String.trimParentheses() = trimStart('(').trimEnd(')')

/**
 * Default delimiters of lists in OptiGUI and OptiFine resources.
 *
 * @see splitIgnoreEmpty
 */
val delimiters = charArrayOf(' ', '\t')

/**
 * Splits a string at the given delimiters and returns every substring, which is not empty.
 */
fun CharSequence.splitIgnoreEmpty(vararg delimiters: Char) =
    this.split(*delimiters).filter { it.isNotEmpty() }

/**
 * Check whether now is Christmas.
 */
fun isChristmas(): Boolean = LocalDateTime.now().let { it.month == Month.DECEMBER && it.dayOfMonth in 24..26 }
