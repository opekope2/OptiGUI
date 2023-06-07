package opekope2.optigui.resource

import opekope2.optigui.exception.ResourceNotFoundException
import opekope2.optigui.internal.logger
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
     * This method must be overwritten.
     *
     * @throws ResourceNotFoundException If the resource doesn't exist.
     */
    @get: Throws(ResourceNotFoundException::class)
    open val ini: Ini =
        when (javaClass) {
            OptiGuiResource::class.java -> Ini().apply { load(resource.inputStream) }
            else -> Ini()
        }
        get() {
            if (javaClass != OptiGuiResource::class.java) {
                // If we are here, then this getter isn't overwritten
                logger.warn("${javaClass.name} doesn't override ${OptiGuiResource::class.java.name}.${::ini.name}! Returning dummy INI.")
            }
            return field
        }
}
