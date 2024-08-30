package opekope2.optigui.resource.loader

import net.minecraft.resource.Resource
import net.minecraft.util.Identifier
import org.slf4j.Logger
import java.io.InputStream

/**
 * A resource loader for loading filters from resource packs.
 */
interface IResourceLoader<T> {
    /**
     * Returns the starting folder where the resource search should be started.
     *
     * For example, the return value `"textures"` represents `/assets/?/textures` folders, where `?` may be any folder
     * (it's the namespace of the resource to be loaded).
     * Further filtering takes place in [canLoad] (called by Minecraft).
     */
    val startingPath: String

    /**
     * Returns if this resource loader can load a resource at the given path.
     *
     * @param resourceId The path to the resource to load
     */
    fun canLoad(resourceId: Identifier): Boolean

    /**
     * Loads the given resource and returns its in-memory representation in the
     * [Prepare stage](https://fabricmc.net/wiki/tutorial:resource#prepare_stage).
     *
     * This method may be called multiple times.
     *
     * @param resourceId The resource identifier
     * @param resource The resource to be loaded
     * @param logger The logger used to log resource loading events
     * @return The loaded resource, which will be passed to [processResource], unless it's `null`
     * @implNote After reading the resource, its input stream must be closed with [InputStream.close] to avoid resource
     * leaks
     */
    fun loadResource(resourceId: Identifier, resource: Resource, logger: Logger): T?

    /**
     * Processes the loaded resource from [loadResource] in the
     * [Apply stage](https://fabricmc.net/wiki/tutorial:resource#apply_stage).
     *
     * This method may be called multiple times.
     *
     * @param context The resource loading context containing the loaded resource returned from [loadResource]
     */
    fun processResource(context: IResourceLoadingContext<T>)
}
