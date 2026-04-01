package opekope2.optigui.resource

import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.item.DyeColor
import net.minecraft.resources.ResourceLocation
import opekope2.optigui.internal.util.delimiters
import opekope2.optigui.internal.util.eventBuilder
import opekope2.optigui.internal.util.splitIgnoreEmpty
import opekope2.optigui.internal.util.toBoolean
import opekope2.optigui.registry.ContainerDefaultGuiTextureRegistry
import opekope2.optigui.util.*
import org.ini4j.Options
import org.slf4j.Logger
import org.slf4j.event.Level.WARN

/**
 * OptiFine properties filter loader.
 */
class OptiFineFilterLoader : IFilterLoader {
    override fun loadRawFilters(resourceManager: ResourceManager, logger: Logger): Collection<IRawFilterData> {
        return resourceManager.listResources(OPTIFINE_RESOURCES_ROOT) { (ns, path) ->
            ns == ResourceLocation.DEFAULT_NAMESPACE && path.endsWith(".properties")
        }.flatMap { (id, resource) ->
            createFilterData(resourceManager, id, resource.open().use(::Options), logger)
        }
    }
}

private fun createFilterData(
    resourceManager: ResourceManager,
    resourcePath: ResourceLocation,
    properties: Options,
    logger: Logger
) = sequence {
    if (properties["optigui.ignore"] == "true") return@sequence

    val container = properties["container"] ?: return@sequence
    val texture = properties["texture"]
    val texturesWithPath = properties.entries.mapNotNull { (key, value) ->
        if (key.startsWith("texture.")) {
            val partialPath = key.substring("texture.".length)
            var fullPath = "textures/gui/$partialPath"
            if (!fullPath.endsWith(".png")) fullPath += ".png"
            Triple(key, ResourceLocation.tryParse(fullPath) ?: return@mapNotNull null, value)
        } else null
    }

    if (texture != null) {
        val resolvedReplacement = resolveTexture(resourceManager, texture, resourcePath)
        if (resolvedReplacement != null) {
            yieldAll(createFilterData(resourcePath, container, resolvedReplacement, properties))
        } else {
            logger.eventBuilder(WARN, resourcePath, container).log(
                "Ignoring container `{}` in `{}`, because replacement texture `{}` cannot be resolved",
                container, resourcePath, texture
            )
        }
    }

    for ((key, original, replacement) in texturesWithPath) {
        val resolvedReplacement = resolveTexture(resourceManager, replacement, resourcePath)
        if (resolvedReplacement == null) {
            logger.eventBuilder(WARN, resourcePath, container).log(
                "Ignoring `{}` in `{}`, because replacement texture `{}` cannot be resolved",
                key, resourcePath, replacement
            )
            continue
        }
        yield(TexturePathFilterData(resourcePath, original, resolvedReplacement, properties))
    }
}

private val dispenserVariants = setOf("dispenser", "dropper")
private val horseVariants = setOf("horse", "donkey", "mule", "llama")

private fun createFilterData(
    resource: ResourceLocation,
    container: String,
    replacementTexture: ResourceLocation,
    properties: Options
) = sequence {
    when (container) {
        "anvil" -> {
            yield(OptiFineFilterData(resource, "anvil", replacementTexture, properties, false))
            yield(OptiFineFilterData(resource, "chipped_anvil", replacementTexture, properties, false))
            yield(OptiFineFilterData(resource, "damaged_anvil", replacementTexture, properties, false))
        }

        "beacon" -> yield(BeaconFilterData(resource, replacementTexture, properties))
        "brewing_stand" -> yield(OptiFineFilterData(resource, "brewing_stand", replacementTexture, properties, true))
        "chest" -> {
            // Lots of pain and suffering from this retarded syntax, thanks OptiFine
            val large = properties["large"].toBoolean()
            val trapped = properties["trapped"].toBoolean()
            val ender = properties["ender"].toBoolean()

            if (ender != true) {
                if (trapped != true) {
                    yield(ChestFilterData(resource, "chest", replacementTexture, properties))
                }
                if (trapped != false) {
                    yield(ChestFilterData(resource, "trapped_chest", replacementTexture, properties))
                }
            }
            // Ender chests can't be large, trapped, and can't be named
            if (ender != false && large != true && trapped != true && properties["name"].isNullOrEmpty()) {
                yield(OptiFineFilterData(resource, "ender_chest", replacementTexture, properties, false))
            }
        }

        "crafting" -> yield(OptiFineFilterData(resource, "crafting_table", replacementTexture, properties, false))
        "dispenser" -> {
            val variants = properties["variants"]
                ?.splitIgnoreEmpty(*delimiters)
                ?.toSet()
                ?.filter { it in dispenserVariants }
                ?: listOf("dispenser")
            for (variant in variants) {
                yield(OptiFineFilterData(resource, variant, replacementTexture, properties, true))
            }
        }

        "enchantment" -> yield(OptiFineFilterData(resource, "enchanting_table", replacementTexture, properties, true))
        "furnace" -> yield(OptiFineFilterData(resource, "furnace", replacementTexture, properties, true))
        "hopper" -> yield(OptiFineFilterData(resource, "hopper", replacementTexture, properties, true))
        "horse" -> {
            val entities = properties["variants"]
                ?.splitIgnoreEmpty(*delimiters)
                ?.filter { it in horseVariants }
                ?.toMutableSet()
                ?: horseVariants.toMutableSet()
            if (entities.remove("llama")) {
                yield(LlamaFilterData(resource, replacementTexture, properties))
            }
            for (entity in entities) {
                yield(OptiFineFilterData(resource, entity, replacementTexture, properties, true))
            }
        }

        "villager" -> yield(VillagerFilterData(resource, replacementTexture, properties))
        "shulker_box" -> {
            val shulkerBoxes = properties["colors"]
                ?.splitIgnoreEmpty(*delimiters)
                ?.toSet()
                ?.filter { DyeColor.byName(it, null) != null }
                ?.map { "${it}_shulker_box" }
                ?: listOf("shulker_box")
            for (shulkerBox in shulkerBoxes) {
                yield(OptiFineFilterData(resource, shulkerBox, replacementTexture, properties, true))
            }
        }

        "creative" -> {}
        "inventory" -> yield(PlayerFilterData(resource, replacementTexture, properties))
    }
}

private fun resolveTexture(
    resourceManager: ResourceManager,
    texture: String,
    resourcePath: ResourceLocation
): ResourceLocation? {
    var texturePath = resolvePath(texture, resourcePath, OPTIFINE_TILDE_PATH) ?: return null

    if (resourceManager.getResource(texturePath).isPresent) return texturePath

    texturePath = texturePath.run { ResourceLocation.fromNamespaceAndPath(namespace, "$path.png") }

    return if (resourceManager.getResource(texturePath).isPresent) texturePath
    else null
}

private open class OptiFineFilterData(
    override val resource: ResourceLocation,
    final override val container: ResourceLocation?,
    final override var replacementTexture: ResourceLocation,
    protected val properties: Options,
    private val filterName: Boolean
) : IRawFilterData {
    override var replaceableTextures: Set<ResourceLocation> =
        container?.let(ContainerDefaultGuiTextureRegistry::get)?.let(::setOf) ?: setOf()

    constructor(
        resource: ResourceLocation,
        container: String,
        replacementTexture: ResourceLocation,
        properties: Options,
        filterName: Boolean
    ) : this(resource, ResourceLocation.withDefaultNamespace(container), replacementTexture, properties, filterName)

    open val originalTexture: ResourceLocation? = container?.let(ContainerDefaultGuiTextureRegistry::get)

    override val rawSelectorData
        get() = sequence {
            originalTexture?.let { original -> yield("interaction.texture" to original.toString()) }
            properties["biomes"]?.let { biomes -> yield("biomes" to biomes) }
            properties["heights"]?.let { heights -> yield("heights" to heights) }
            if (filterName) {
                properties["name"]?.let { name -> yield(getNameSelectorData(name)) }
            }
        }.asIterable()
}

private class BeaconFilterData(
    resource: ResourceLocation,
    replacementTexture: ResourceLocation,
    properties: Options
) : OptiFineFilterData(resource, "beacon", replacementTexture, properties, true) {
    override val rawSelectorData
        get() = sequence {
            yieldAll(super.rawSelectorData)
            properties["levels"]?.let { levels -> yield("beacon.levels" to levels) }
        }.asIterable()
}

private class ChestFilterData(
    resource: ResourceLocation,
    container: String,
    replacementTexture: ResourceLocation,
    properties: Options
) : OptiFineFilterData(resource, container, replacementTexture, properties, true) {
    override val rawSelectorData
        get() = sequence {
            yieldAll(super.rawSelectorData)
            properties["large"]?.let { large -> yield("chest.large" to large) }
            properties["christmas"]?.let { christmas ->
                when (christmas.toBoolean()) {
                    true -> yield("date" to "dec@24-26")
                    false -> yield("date" to "1 2 3 4 5 6 7 8 9 spooktober 11 dec@1-23 dec@27-31")
                    null -> {}
                }
            }
        }.asIterable()
}

private class LlamaFilterData(
    resource: ResourceLocation,
    replacementTexture: ResourceLocation,
    properties: Options
) : OptiFineFilterData(resource, "llama", replacementTexture, properties, true) {
    override val rawSelectorData
        get() = sequence {
            yieldAll(super.rawSelectorData)
            properties["colors"]?.let { colors -> yield("llama.colors" to colors) }
        }.asIterable()
}

private class VillagerFilterData(
    resource: ResourceLocation,
    replacementTexture: ResourceLocation,
    properties: Options
) : OptiFineFilterData(resource, "villager", replacementTexture, properties, true) {
    override val rawSelectorData
        get() = sequence {
            yieldAll(super.rawSelectorData)
            professionSelectors.takeIf { it.isNotEmpty() }?.let { professions ->
                yield("villager.professions" to professions.joinToString(" "))
            }
        }.asIterable()

    private val professionSelectors: Set<String>
        get() = sequence {
            properties["professions"]
                ?.splitIgnoreEmpty(*delimiters)
                ?.map { it.split(":") }
                ?.forEach { prof ->
                    when (prof.size) {
                        1 -> yield(prof[0])
                        2 -> prof[1].splitIgnoreEmpty(',')?.forEach {
                            yield("${prof[0]}@$it")
                        }
                    }
                }
        }.toSet()
}

private class PlayerFilterData(
    resource: ResourceLocation,
    replacementTexture: ResourceLocation,
    properties: Options
) : OptiFineFilterData(resource, "player", replacementTexture, properties, false) {
    override val rawSelectorData
        get() = sequence {
            originalTexture?.let { original -> yield("interaction.texture" to original.toString()) }
            properties["biomes"]?.let { biomes -> yield("player.biomes" to biomes) }
            properties["heights"]?.let { heights -> yield("player.heights" to heights) }
        }.asIterable()
}

private class TexturePathFilterData(
    resource: ResourceLocation,
    override val originalTexture: ResourceLocation,
    replacementTexture: ResourceLocation,
    properties: Options
) : OptiFineFilterData(resource, null, replacementTexture, properties, false) {
    init {
        replaceableTextures = setOf(originalTexture)
    }

    override val rawSelectorData: Iterable<Pair<String, String>>
        get() = sequence {
            yield("interaction.texture" to originalTexture.toString())
            properties["biomes"]?.let { biomes -> yield("player.biomes" to biomes) }
            properties["heights"]?.let { heights -> yield("player.heights" to heights) }
        }.asIterable()
}

private fun getNameSelectorData(name: String) = when {
    name.startsWith("pattern:") -> "name.wildcard" to name.substring("pattern:".length)
    name.startsWith("ipattern:") -> "name.wildcard.ignore_case" to name.substring("ipattern:".length)
    name.startsWith("regex:") -> "name.regex" to name.substring("regex:".length)
    name.startsWith("iregex:") -> "name.regex.ignore_case" to name.substring("iregex:".length)
    else -> "name" to name
}
