package opekope2.optigui.exception

import net.minecraft.util.Identifier
import opekope2.optigui.resource.Resource

/**
 * An exception thrown by [Resource] when it was not found.
 *
 * @param resource The resource path, which was not found
 */
class ResourceNotFoundException(val resource: Identifier) : Exception("Resource '$resource' doesn't exist.")
