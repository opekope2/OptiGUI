package opekope2.util

import opekope2.filter.FilterResult

fun <T> catchAll(function: () -> T): T? = try {
    function()
} catch (_: Throwable) {
    null
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
