package opekope2.util

import net.minecraft.util.Identifier
import opekope2.filter.FilterResult
import opekope2.optigui.resource.IResourceManager

/**
 * Runs a code block and returns its result.
 * If it raises and exception, suppresses it exceptions, and returns `null`
 */
internal inline fun <T> catchAll(function: () -> T): T? = try {
    function()
} catch (_: Exception) {
    null
}

/**
 * Converts the given string to a boolean:
 * - `true`, if the string is "true" (case-insensitive)
 * - `false`, if the string is "false" (case-insensitive)
 * - `null` otherwise
 */
internal fun String.toBoolean(): Boolean? = when (lowercase()) {
    "true" -> true
    "false" -> false
    else -> null
}

/**
 * Resolves an OptiFine-compatible PNG image resource by appending the extension if necessary.
 *
 * @param id The resource identifier
 * @return The found identifier or `null` if not found
 */
internal fun IResourceManager.resolveResource(id: Identifier?): Identifier? {
    if (resourceExists(id ?: return null)) return id

    val idPng = Identifier(id.namespace, "${id.path}.png")
    return if (resourceExists(idPng)) idPng else null
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
