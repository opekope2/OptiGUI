package opekope2.optigui.internal.resource.loader

import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import opekope2.optigui.filter.IInteractionFilter
import opekope2.optigui.internal.filter.TextureReplacerFilter
import opekope2.optigui.resource.IResourceLoadingContext
import org.slf4j.Logger
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import kotlin.jvm.optionals.getOrNull

internal class ResourceLoadingContext<T>(
    private val resourceManager: ResourceManager,
    private val loadedResourceGetter: Function<Identifier, Any?>,
    private val replaceableTextureAdder: Consumer<Identifier>,
    private val containerFilterAdder: BiConsumer<Identifier, TextureReplacerFilter>,
    override val resourceId: Identifier,
    override val resource: T,
    override val logger: Logger
) : IResourceLoadingContext<T> {
    override fun getResource(resourceId: Identifier) = resourceManager.getResource(resourceId).getOrNull()

    override fun getLoadedResource(resourceId: Identifier) = loadedResourceGetter.apply(resourceId)

    override fun addReplaceableTexture(textureId: Identifier) {
        replaceableTextureAdder.accept(textureId)
    }

    override fun addRetexturableContainer(
        containerId: Identifier,
        filter: IInteractionFilter,
        replacementTexture: Identifier,
        priority: Int
    ) {
        if (priority < 0) throw IllegalArgumentException("Priority can't be negative")
        containerFilterAdder.accept(
            containerId,
            TextureReplacerFilter(filter, replacementTexture, priority)
        )
    }
}
