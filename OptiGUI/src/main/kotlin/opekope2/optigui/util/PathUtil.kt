@file: JvmName("PathUtil")

package opekope2.optigui.util

import net.minecraft.util.Identifier
import net.minecraft.util.InvalidIdentifierException
import java.nio.file.Path

/**
 * Resolves the absolute path of the given resource to resolve.
 *
 * @param pathToResolve The path to resolve
 * @param resource The resource file to start resolving at
 * @param tildePath The path `~` represents, or `null` to disable this feature
 * @return The found resource or `null`, if the path is malformed
 */
@JvmOverloads
fun resolvePath(pathToResolve: String, resource: Identifier, tildePath: String? = null): Identifier? {
    val tildeValid = tildePath != null && pathToResolve.startsWith("~/")
    val root = Path.of(
        if (tildeValid) "$tildePath/."
        else resource.path
    )
    val toResolve =
        if (tildeValid) pathToResolve.substring(2)
        else pathToResolve

    if (toResolve.startsWith('/')) return null

    return when (toResolve.count { it == ':' }) {
        0 -> try {
            val path = root.resolveSibling(toResolve).normalize().toString().replace('\\', '/')

            if (path.contains("..")) null
            else Identifier.of(resource.namespace, path)
        } catch (_: InvalidIdentifierException) {
            null
        }

        1 -> Identifier.tryParse(toResolve)

        else -> return null
    }
}
