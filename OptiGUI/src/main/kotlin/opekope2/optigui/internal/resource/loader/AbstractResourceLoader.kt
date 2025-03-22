package opekope2.optigui.internal.resource.loader

import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.MissingSprite
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceFinder
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SinglePreparationResourceReloader
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import opekope2.optigui.filter.IFilterLoader
import opekope2.optigui.filter.TextureChangerFilter
import org.slf4j.Logger

private typealias TextureChangerFilterList = List<TextureChangerFilter>

internal abstract class AbstractResourceLoader(val id: Identifier) :
    SinglePreparationResourceReloader<TextureChangerFilterList>(), IFilterLoader {
    init {
        IFilterLoader.Registry.register(id, this)
    }

    protected abstract val logger: Logger
    protected abstract val finder: ResourceFinder

    protected lateinit var filters: TextureChangerFilterList

    override fun prepare(manager: ResourceManager, profiler: Profiler) =
        finder.findResources(manager).flatMap { (id, resource) ->
            try {
                loadFilters(id, resource, manager)
            } catch (e: Exception) {
                logger.error("Error loading resource {}", id, e)
                listOf()
            }
        }

    protected abstract fun loadFilters(
        resourceId: Identifier,
        resource: Resource,
        manager: ResourceManager
    ): Collection<TextureChangerFilter>

    override fun apply(prepared: TextureChangerFilterList, manager: ResourceManager, profiler: Profiler) {
        val guiAtlasManager = MinecraftClient.getInstance().guiAtlasManager
        val missingSprite = guiAtlasManager.getSprite(MissingSprite.getMissingSpriteId())

        filters = prepared.map { filter ->
            val missingTextures = filter.textureChanges.values.filterTo(mutableSetOf()) {
                manager.getResource(it).isEmpty
            }
            if (missingTextures.isNotEmpty())
                logger.warn("Missing textures in {}: {}", filter.resourceId, missingTextures.joinToString())

            val missingSprites = filter.spriteChanges.values.filterTo(mutableSetOf()) {
                guiAtlasManager.getSprite(it) === missingSprite
            }
            if (missingSprites.isNotEmpty())
                logger.warn("Missing sprites in {}: {}", filter.resourceId, missingTextures.joinToString())

            filter.copy(
                textureChanges = filter.textureChanges.filter { (_, value) -> manager.getResource(value).isPresent },
                spriteChanges = filter.spriteChanges.filter { (_, value) -> guiAtlasManager.getSprite(value) !== missingSprites }
            )
        }
    }

    override fun get() = filters
}
