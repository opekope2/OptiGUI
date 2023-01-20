package opekope2.optigui.resource

import net.minecraft.util.Identifier
import opekope2.optigui.exception.ResourceNotFoundException
import java.io.InputStream
import kotlin.jvm.Throws

/**
 * A resource manager wrapper interface for [net.minecraft.resource.ResourceManager]
 */
interface IResourceManager {
    /**
     * Returns whether the given resource ID exists.
     *
     * @param resourceId The requested resource ID
     */
    fun resourceExists(resourceId: Identifier): Boolean

    /**
     * Returns the resource pack name the given resource is loaded from. If the resource doesn't exist, throws an exception.
     *
     * @param resourceId The requested resource
     */
    @Throws(ResourceNotFoundException::class)
    fun resourcePackName(resourceId: Identifier): String

    /**
     * Gets the input stream the resource can be read from. If the resource doesn't exist, throws an exception.
     */
    @Throws(ResourceNotFoundException::class)
    fun getInputStream(resourceId: Identifier): InputStream

    /**
     * Finds all resources starting at [startPath], which match the given predicate.
     *
     * @param startPath The path to start search from
     * @param pathPredicate The predicate to decide to include path in results or not
     */
    fun findResources(startPath: String, pathPredicate: (Identifier) -> Boolean): Collection<Identifier>
}
