package opekope2.util

import net.minecraft.util.Identifier
import java.util.*

/**
 * Resolves a texture path starting at [resourcePath] if it's relative. Ported from v1 codebase.
 * Legacy code, if it works, then don't touch it.
 *
 * @param resourcePath The starting path
 * @param path The path to expand to an absolute path
 * @return The absolute path to the resource or `null` if it escapes the root with `..`
 */
fun resolvePath(resourcePath: String, path: String): Identifier? {
    val pathStack: Deque<String> = ArrayDeque()
    var tokenizer = StringTokenizer(resourcePath, "/")
    while (tokenizer.hasMoreTokens()) {
        pathStack.push(tokenizer.nextToken())
    }

    // Because there was a resource pack with two dangling tab characters after the resource name
    tokenizer = StringTokenizer(path.trim(), ":/", true)
    var namespace = Identifier.DEFAULT_NAMESPACE
    var nToken = -1

    while (tokenizer.hasMoreTokens()) {
        val token = tokenizer.nextToken()
        nToken++

        if (nToken == 0 && "~" == token) {
            pathStack.clear()
            pathStack.push("optifine")
            continue
        } else if (":" == token) {
            if (nToken == 1) {
                namespace = pathStack.pop()
                pathStack.clear()
            }
        } else if (".." == token) {
            if (pathStack.isEmpty()) return null

            pathStack.pop()
        } else if ("/" != token && "." != token) {
            pathStack.push(token)
        }
    }

    val pathBuilder = StringBuilder()
    var first = true

    while (!pathStack.isEmpty()) {
        if (first) {
            first = false
        } else {
            pathBuilder.append("/")
        }
        pathBuilder.append(pathStack.removeLast())
    }

    return Identifier(namespace, pathBuilder.toString())
}
