package opekope2.optigui.resource

import opekope2.optigui.exception.ResourceNotFoundException
import org.ini4j.Ini

/**
 * Represents an OptiGUI-compatible INI file.
 */
open class OptiGuiResource(private val resource: ResourceReader) : Resource(resource.id) {
    override fun exists(): Boolean = resource.exists()

    override val resourcePack: String by resource::resourcePack

    /**
     * Gets the loaded resource as an INI object.
     *
     * @throws ResourceNotFoundException If the resource doesn't exist.
     */
    @get: Throws(ResourceNotFoundException::class)
    open val ini: Ini by lazy { Ini().apply { load(resource.inputStream) } }
}
