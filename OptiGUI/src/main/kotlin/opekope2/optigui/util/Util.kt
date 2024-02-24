package opekope2.optigui.util

import java.io.StringWriter

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
 * Same as [kotlin.text.buildString], but with [StringWriter].
 */
inline fun buildString(builderAction: StringWriter.() -> Unit): String {
    return StringWriter().apply(builderAction).toString()
}
