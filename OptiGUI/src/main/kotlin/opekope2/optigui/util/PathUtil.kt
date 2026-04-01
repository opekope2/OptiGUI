@file: JvmName("PathUtil")

package opekope2.optigui.util

import net.minecraft.resources.ResourceLocation
import net.minecraft.ResourceLocationException
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
fun resolvePath(pathToResolve: String, resource: ResourceLocation, tildePath: String? = null): ResourceLocation? {
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
            else ResourceLocation.fromNamespaceAndPath(resource.namespace, path)
        } catch (_: ResourceLocationException) {
            null
        }

        1 -> ResourceLocation.tryParse(toResolve)

        else -> return null
    }
}
