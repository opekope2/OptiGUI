package opekope2.optigui.internal.mc_1_19_3

import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.optigui.exception.ResourceNotFoundException
import opekope2.optigui.provider.IResourceManagerProvider
import opekope2.optigui.resource.IResourceManager
import java.io.InputStream

// TODO dynamic linker
internal class ResourceManagerProvider : IResourceManagerProvider {
    override fun wrapResourceManager(resourceManager: ResourceManager): IResourceManager =
        ResourceManagerWrapper(resourceManager)

    private class ResourceManagerWrapper(private val resourceManager: ResourceManager) : IResourceManager {
        override fun resourceExists(resourceId: Identifier): Boolean = resourceManager.getResource(resourceId).isPresent

        override fun resourcePackName(resourceId: Identifier): String = resourceManager.getResource(resourceId)
            .let { if (it.isPresent) it.get().resourcePackName else throwResourceNotFound(resourceId) }

        override fun getInputStream(resourceId: Identifier): InputStream =
            resourceManager.getResource(resourceId)?.let { if (it.isPresent) it.get().inputStream else null }
                ?: throwResourceNotFound(resourceId)

        override fun findResources(startPath: String, pathPredicate: (Identifier) -> Boolean): Collection<Identifier> =
            resourceManager.findResources(startPath, pathPredicate).map { it.key }

        private fun throwResourceNotFound(resourceId: Identifier): Nothing =
            throw ResourceNotFoundException("Resource '${resourceId}' doesn't exist!")
    }
}
