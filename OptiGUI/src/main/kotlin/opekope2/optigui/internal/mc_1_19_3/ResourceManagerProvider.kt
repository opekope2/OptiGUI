package opekope2.optigui.internal.mc_1_19_3

import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.linker.FabricDynamicLinker
import opekope2.optigui.exception.ResourceNotFoundException
import opekope2.optigui.provider.IResourceManagerProvider
import opekope2.optigui.resource.IResourceManager
import java.io.InputStream
import java.util.*
import java.util.function.Predicate

internal class ResourceManagerProvider : IResourceManagerProvider {
    override fun wrapResourceManager(resourceManager: ResourceManager): IResourceManager =
        ResourceManagerWrapper(resourceManager)

    private class ResourceManagerWrapper(private val resourceManager: ResourceManager) : IResourceManager {
        /**
         * [Documentation (1.19.3)](https://maven.fabricmc.net/docs/yarn-1.19.3+build.5/net/minecraft/resource/ResourceFactory.html#getResource(net.minecraft.util.Identifier))
         */
        private val getResourceMethod =
            resourceFactoryLinker.findVirtualMethod("method_14486", Optional::class.java, Identifier::class.java)
                ?: throwLinkerError()

        /**
         * [Documentation (1.19.3)](https://maven.fabricmc.net/docs/yarn-1.19.3+build.5/net/minecraft/resource/ResourceManager.html#findResources(java.lang.String,java.util.function.Predicate))
         */
        private val findResourcesMethod =
            resourceManagerLinker.findVirtualMethod(
                "method_14488",
                java.util.Map::class.java,
                java.lang.String::class.java,
                Predicate::class.java
            ) ?: throwLinkerError()

        fun getResource(id: Identifier) = getResourceMethod.invoke(resourceManager, id) as Optional<Resource>

        override fun resourceExists(resourceId: Identifier): Boolean = getResource(resourceId).isPresent

        override fun resourcePackName(resourceId: Identifier): String = getResource(resourceId)
            .let { if (it.isPresent) it.get().resourcePackName else throwResourceNotFound(resourceId) }

        override fun getInputStream(resourceId: Identifier): InputStream =
            getResource(resourceId).let { if (it.isPresent) it.get().inputStream else null }
                ?: throwResourceNotFound(resourceId)

        override fun findResources(startPath: String, pathPredicate: (Identifier) -> Boolean): Collection<Identifier> =
            (findResourcesMethod.invoke(
                resourceManager,
                startPath,
                Predicate<Identifier> { pathPredicate(it) }
            ) as Map<Identifier, Resource>).map { it.key }

        private fun throwResourceNotFound(resourceId: Identifier): Nothing =
            throw ResourceNotFoundException("Resource '${resourceId}' doesn't exist!")

        private fun throwLinkerError(): Nothing = throw ResourceNotFoundException("Failed to link ResourceManager.")
    }

    private companion object {
        /**
         * [Documentation (1.19.3)](https://maven.fabricmc.net/docs/yarn-1.19.3+build.5/net/minecraft/resource/ResourceFactory.html)
         */
        val resourceFactoryLinker = FabricDynamicLinker("net.minecraft.class_5912")

        /**
         * [Documentation (1.19.3)](https://maven.fabricmc.net/docs/yarn-1.19.3+build.5/net/minecraft/resource/ResourceManager.html)
         */
        val resourceManagerLinker = FabricDynamicLinker("net.minecraft.class_3300")
    }
}
