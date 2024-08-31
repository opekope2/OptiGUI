package opekope2.optigui.internal.resource.loader.sdlang

import com.google.common.collect.ImmutableList
import com.singingbush.sdl.Parser
import com.singingbush.sdl.Tag
import net.fabricmc.api.ClientModInitializer
import net.minecraft.resource.Resource
import net.minecraft.util.Identifier
import opekope2.optigui.filter.*
import opekope2.optigui.interaction.InteractionFilterFactories
import opekope2.optigui.resource.loader.IResourceLoader
import opekope2.optigui.resource.loader.IResourceLoadingContext
import opekope2.optigui.resource.loader.ResourceLoaders
import opekope2.optigui.resource.matcher.nbt.NbtMatchers
import opekope2.optigui.util.*
import org.slf4j.Logger
import java.io.InputStreamReader
import kotlin.collections.Collection
import kotlin.collections.MutableList
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.contains
import kotlin.collections.isNotEmpty
import kotlin.collections.iterator
import kotlin.collections.joinToString
import kotlin.collections.mapValues
import kotlin.collections.mutableListOf
import kotlin.collections.plusAssign
import kotlin.collections.set

internal class SDLangResourceLoader : IResourceLoader<SDLangDocument>, ClientModInitializer {
    override val startingPath: String
        get() = OPTIGUI_RESOURCES_ROOT

    override fun canLoad(resourceId: Identifier) = resourceId.namespace == MOD_ID && resourceId.path.endsWith(".sdl")

    override fun loadResource(resourceId: Identifier, resource: Resource, logger: Logger): SDLangDocument? = try {
        val parser = Parser(InputStreamReader(resource.inputStream))
        SDLangDocument(parser.parse()) // Closes inputStream
    } catch (e: Exception) {
        logger.error("Error loading resource", e)
        null
    }

    override fun processResource(context: IResourceLoadingContext<SDLangDocument>) {
        for (tag in context.loadedResource.tags) {
            when (tag.name) {
                "const" -> processConstTag(tag, context)
                "retexture" -> processRetextureTag(tag, context)
                else -> context.logger.warn("Ignoring unexpected tag `{}`", tag.name)
            }
        }
    }

    override fun onInitializeClient() {
        ResourceLoaders.register(this)
    }

    private fun processConstTag(tag: Tag, ctx: IResourceLoadingContext<SDLangDocument>) {
        when (val name = tag.getAttribute("name")) {
            null -> ctx.logger.error("Constant name is missing")
            !is String -> ctx.logger.error("Constant name `{}` is not a string", name)
            in ctx.loadedResource.constants -> ctx.logger.error("Constant `{}` is already defined", name)
            else -> ctx.loadedResource.constants[name] = ImmutableList.copyOf(tag.values)
        }
    }

    private fun processRetextureTag(tag: Tag, ctx: IResourceLoadingContext<SDLangDocument>) {
        val containers = getContainers(tag, ctx)

        var error = false
        val filters = mutableListOf<IInteractionFilter>()
        val loadTimeFilters = mutableListOf<ILoadTimeFilter>()

        for (child in tag.children) {
            val childFilters = createChildFilters(child, ctx).ifNull { error = true } ?: continue

            when (child.name) {
                in LoadTimeFilterFactories -> {
                    val loadTimeFilter = createLoadTimeFilter(tag, childFilters, ctx.logger)
                        .ifNull { error = true } ?: continue

                    loadTimeFilters += loadTimeFilter
                }

                in InteractionFilterFactories -> {
                    val filter = createInteractionFilter(child, childFilters, ctx.logger)
                        .ifNull { error = true } ?: continue

                    filters += filter
                }

                else -> {
                    error = true
                    ctx.logger.error("Tag name `{}` was not recognized", child.name)
                }
            }
        }

        val replacement = getReplacementTexture(tag, ctx) ?: return

        if (error) return

        val loadTimeFilter = FilterCollectionFilter(loadTimeFilters, PredicateCollectionOperators.all())
        if (!loadTimeFilter.test(null)) return

        val interactionFilter = FilterCollectionFilter(filters, PredicateCollectionOperators.all())
        for (containerId in containers) {
            ctx.addRetexturableContainer(containerId, interactionFilter, replacement, 0)
        }
    }

    private fun getContainers(tag: Tag, ctx: IResourceLoadingContext<SDLangDocument>): MutableList<Identifier> {
        val containers = mutableListOf<Identifier>()
        val invalidContainers = mutableListOf<Any?>()

        for (container in tag.values) {
            if (container !is String) invalidContainers += container
            else when (val containerId = Identifier.tryParse(container)) {
                null -> invalidContainers += container
                else -> containers += containerId
            }
        }

        if (invalidContainers.isNotEmpty()) {
            ctx.logger.warn("Ignoring malformed containers: {}", invalidContainers.joinToString { "`$it`" })
        }

        return containers
    }

    private fun createLoadTimeFilter(tag: Tag, childFilters: Collection<INbtFilter>, logger: Logger): ILoadTimeFilter? {
        val loadTimeFilterFactory = LoadTimeFilterFactories.getValue(tag.name)
        val loadTimeFilter = loadTimeFilterFactory.createLoadTimeFilter(
            tag.values,
            tag.attributes.mapValues { (_, value) -> value.value },
            childFilters,
            logger
        )

        return loadTimeFilter
    }

    private fun createInteractionFilter(
        tag: Tag,
        childFilters: Collection<INbtFilter>,
        logger: Logger
    ): IInteractionFilter? {
        val factory = InteractionFilterFactories.getValue(tag.name)
        val filter = factory.createInteractionFilter(
            tag.values,
            tag.attributes.mapValues { (_, value) -> value.value },
            childFilters,
            logger
        )

        return filter
    }

    private fun getReplacementTexture(tag: Tag, ctx: IResourceLoadingContext<SDLangDocument>): Identifier? {
        val replacement = tag.getAttribute("replacement")

        if (replacement == null) {
            ctx.logger.error("Replacement texture is not specified")
            return null
        } else if (replacement !is String) {
            ctx.logger.error("Replacement texture `{}` is not a string", replacement)
            return null
        }

        val replacementPath = resolvePath(replacement, ctx.resourceId)

        if (replacementPath == null) {
            ctx.logger.error("Replacement texture `{}` cannot be resolved", replacement)
        } else if (ctx.getResource(replacementPath) == null) {
            ctx.logger.error("Replacement texture `{}` (resolved as `{}`) does not exist", replacement, replacementPath)
        } else return replacementPath

        return null
    }

    private fun createChildFilters(tag: Tag, ctx: IResourceLoadingContext<SDLangDocument>): Collection<INbtFilter>? {
        var error = false
        val filters = mutableListOf<INbtFilter>()

        for (child in tag.children) {
            filters += createFilter(child, ctx).ifNull { error = true } ?: continue
        }

        return if (error) null
        else ImmutableList.copyOf(filters)
    }

    private fun createFilter(tag: Tag, ctx: IResourceLoadingContext<SDLangDocument>): INbtFilter? {
        var error = false
        val subNbtKey = if (tag.name == "content") tag.value else tag.name // Default name is `content` if anonymous
        val filters = mutableListOf<INbtFilter>()

        if (subNbtKey !is String && subNbtKey !is Int) {
            ctx.logger.error("Tag name `{}` is not a string or an integer", subNbtKey)
            error = true
        }

        for ((matcherName, parameter) in tag.attributes) {
            val filter = createFilterFromMatcher(matcherName, parameter.value, ctx)

            when {
                filter == null -> error = true
                subNbtKey is String -> filters += SubNbtFilter(subNbtKey, filter)
                subNbtKey is Int -> filters += NbtListIndexFilter(subNbtKey, filter)
            }
        }

        val childFilters = createChildFilters(tag, ctx) ifNull { error = true }

        if (error) return null

        filters.addAll(childFilters!!)

        return FilterCollectionFilter(filters, PredicateCollectionOperators.all())
    }

    private fun createFilterFromMatcher(
        matcherName: String,
        attributeValue: Any,
        ctx: IResourceLoadingContext<SDLangDocument>
    ): INbtFilter? {
        if (matcherName !in NbtMatchers) {
            ctx.logger.error("No such matcher `{}`", matcherName)
            return null
        }
        val matcher = NbtMatchers.getValue(matcherName)

        if (!matcherName.endsWith('$')) return matcher.createFilter(attributeValue)
        return when (attributeValue) {
            !is String -> {
                ctx.logger.error(
                    "Constant reference `{}` in attribute `{}` is not a string", attributeValue, matcherName
                )
                null
            }

            !in ctx.loadedResource.constants -> {
                ctx.logger.error(
                    "Constant reference `{}` in attribute `{}` is not defined", attributeValue, matcherName
                )
                null
            }

            else -> matcher.createFilter(ctx.loadedResource.constants[attributeValue]!!)
        }
    }
}
