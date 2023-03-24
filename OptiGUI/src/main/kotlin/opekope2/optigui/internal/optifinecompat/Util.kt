package opekope2.optigui.internal.optifinecompat

import net.minecraft.util.Identifier
import opekope2.optigui.resource.Resource
import opekope2.optigui.service.ResourceResolverService
import opekope2.optigui.service.getService
import opekope2.util.resolvePath
import java.io.File

internal val delimiters = charArrayOf(' ', '\t')

private val resourceResolver: ResourceResolverService by lazy(::getService)
internal fun findReplacementTexture(resource: Resource, texturePath: String): Identifier? {
    val resFolder = File(resource.id.path).parent.replace('\\', '/')

    return resourceResolver.resolveResource(resolvePath(resFolder, texturePath))
}

internal fun findReplacementTexture(resource: Resource): Identifier? {
    return findReplacementTexture(resource, resource.properties["texture"] as? String ?: return null)
}
