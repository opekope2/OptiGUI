package opekope2.util

import net.minecraft.util.Identifier
import opekope2.optigui.resource.IResourceManager

/**
 * Runs a code block and returns its result.
 * If it raises and exception, suppresses it exceptions, and returns `null`
 */
inline fun <T> catchAll(function: () -> T): T? = try {
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
fun String.toBoolean(): Boolean? = when (lowercase()) {
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
fun IResourceManager.resolveResource(id: Identifier?): Identifier? {
    if (resourceExists(id ?: return null)) return id

    val idPng = Identifier(id.namespace, "${id.path}.png")
    return if (resourceExists(idPng)) idPng else null
}
