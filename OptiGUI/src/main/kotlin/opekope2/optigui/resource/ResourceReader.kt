package opekope2.optigui.resource

import net.minecraft.util.Identifier
import opekope2.optigui.exception.ResourceNotFoundException
import java.io.InputStream
import java.util.*

/**
 * A base class for reading resources.
 *
 * @param id The identifier of the resource
 */
abstract class ResourceReader(id: Identifier) : Resource(id), AutoCloseable {

    /**
     * Returns the input steam of the resource.
     *
     * @throws ResourceNotFoundException If the resource doesn't exist
     */
    @get: Throws(ResourceNotFoundException::class)
    abstract val inputStream: InputStream
}
