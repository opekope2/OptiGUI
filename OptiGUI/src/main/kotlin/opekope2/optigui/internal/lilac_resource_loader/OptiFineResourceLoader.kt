package opekope2.optigui.internal.lilac_resource_loader

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import opekope2.lilac.api.resource.IResourceAccess
import opekope2.lilac.api.resource.IResourceReader
import opekope2.lilac.api.resource.loading.IResourceLoader
import opekope2.lilac.api.resource.loading.IResourceLoadingSession
import opekope2.optigui.api.IOptiGuiApi
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.lilac_resource_loading.IOptiGuiExtension
import opekope2.optigui.filter.*
import opekope2.optigui.filter.IFilter.Result.mismatch
import opekope2.optigui.properties.*
import opekope2.util.*
import org.apache.commons.text.StringEscapeUtils.unescapeJava
import org.ini4j.Options
import java.time.Month

@Suppress("unused")
class OptiFineResourceLoader(private val optigui: IOptiGuiExtension) : IResourceLoader {
    override fun getStartingPath(): String = OPTIFINE_RESOURCES_ROOT

    override fun canLoad(resourceName: String): Boolean = resourceName.endsWith(".properties")

    override fun canLoad(resourcePath: Identifier): Boolean =
        resourcePath.namespace == Identifier.DEFAULT_NAMESPACE && resourcePath.path.endsWith(".properties")

    override fun loadResource(resource: IResourceReader): Any = resource.id to resource.inputStream.use(::Options)

    @Suppress("UNCHECKED_CAST")
    override fun processResource(resource: Any) {
        val (resId, props) = resource as Pair<Identifier, Options>

        if (props["optigui.ignore"] == "true") {
            optigui.warn(resId, "Ignoring resource: `optigui.ignore=true`")
        } else {
            loadProps(resId, props)
        }
    }

    private fun loadProps(resource: Identifier, props: Options) {
        fun warn(e: Exception) {
            optigui.warn(resource, e.message ?: e.toString())
        }

        try {
            props["container"]?.let(containerFilterCreators::get)?.invoke(resource, props, optigui)
        } catch (e: Exception) {
            warn(e)
        }
        try {
            DirectFilterCreator(resource, props, optigui)
        } catch (e: Exception) {
            warn(e)
        }
    }

    override fun close() {
        optigui.close()
    }

    companion object Factory : IResourceLoader.IFactory {
        override fun createResourceLoader(session: IResourceLoadingSession): IResourceLoader =
            OptiFineResourceLoader(session["optigui"] as IOptiGuiExtension)
    }
}


private typealias WarnFunction = (String) -> Unit


private open class FilterCreator(private val containers: Set<Identifier>) :
        (Identifier, Options, IOptiGuiExtension) -> Unit {
    constructor(vararg containers: Identifier) : this(setOf(*containers))

    override fun invoke(resource: Identifier, properties: Options, optigui: IOptiGuiExtension) {
        val replacement = resolveReplacementTexture(properties::getValue, resource)

        optigui.addFilter(
            resource,
            PostProcessorFilter(
                ConjunctionFilter(createFilters(properties, optigui.bindWarnTo(resource))),
                replacement
            ),
            containers.mapNotNull(optiguiApi::getContainerTexture).toSet()
        )
    }

    protected open fun createFilters(properties: Options, warn: WarnFunction): MutableList<IFilter<Interaction, *>> {
        val filters = mutableListOf<IFilter<Interaction, *>>()

        filters += PreProcessorFilter(
            { it.container },
            ContainingFilter(containers)
        )
        filters += PreProcessorFilter(
            { it.texture },
            ContainingFilter(containers.mapNotNull(optiguiApi::getContainerTexture))
        )

        if ("name" in properties) {
            val name = properties["name"]!!
            val regex = wrapParseException("name", properties::getValue) {
                when {
                    name.startsWith("pattern:") -> Regex(
                        wildcardToRegex(unescapeJava(name.substring("pattern:".length)))
                    )

                    name.startsWith("ipattern:") -> Regex(
                        wildcardToRegex(unescapeJava(name.substring("ipattern:".length))),
                        RegexOption.IGNORE_CASE
                    )

                    name.startsWith("regex:") -> Regex(
                        unescapeJava(name.substring("regex:".length))
                    )

                    name.startsWith("iregex:") -> Regex(
                        unescapeJava(name.substring("iregex:".length)),
                        RegexOption.IGNORE_CASE
                    )

                    else -> Regex.fromLiteral(name)
                }
            }

            filters += PreProcessorFilter(
                { (it.data as? IGeneralProperties)?.name ?: it.screenTitle.string },
                RegularExpressionFilter(regex)
            )
        }

        properties["biomes"]
            ?.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(Identifier::tryParse) {
                throwParseException("biomes", properties::getValue, "Invalid biomes: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { biomes ->
                filters += PreProcessorFilter.nullGuarded(
                    { (it.data as? IGeneralProperties)?.biome },
                    mismatch(),
                    ContainingFilter(biomes)
                )
            }

        properties["heights"]
            ?.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(NumberOrRange::tryParse) {
                throwParseException("heights", properties::getValue, "Invalid heights: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { heights ->
                filters += PreProcessorFilter.nullGuarded(
                    { (it.data as? IGeneralProperties)?.height },
                    mismatch(),
                    DisjunctionFilter(heights.map { it.toFilter() })
                )
            }

        return filters
    }
}

// For texture.PATH, but naming is hard.
private object DirectFilterCreator : FilterCreator() {
    override fun invoke(resource: Identifier, properties: Options, optigui: IOptiGuiExtension) {
        properties.mapNotNull { (key, value) ->
            if (key.startsWith("texture.")) key.substring("texture.".length) to value
            else null
        }.forEach { (key, value) ->
            val og = "minecraft:textures/gui/" +
                    if (key.endsWith(".png")) key
                    else "$key.png" // Workaround
            val original = wrapParseException("texture.$key", { value }) {
                Identifier(og.substring("texture.".length))
            }
            val replacement = resolveReplacementTexture({ value }, resource, "texture.$key")

            val filters = createFilters(properties, optigui.bindWarnTo(resource))
            filters[1] = PreProcessorFilter(
                { it.texture },
                EqualityFilter(original.toString())
            )
            filters.removeAt(0)

            optigui.addFilter(
                resource,
                PostProcessorFilter(ConjunctionFilter(filters), replacement),
                setOf(original)
            )
        }
    }
}

private val optiguiApi = IOptiGuiApi.getImplementation()
private val resourceAccess = IResourceAccess.getInstance()
private val horseVariants = setOf("horse", "donkey", "mule", "llama")
private val dispenserVariants = setOf("dispenser", "dropper")

private val TEXTURE_GENERIC_54 = optiguiApi.getContainerTexture(Identifier("chest"))!!
private val TEXTURE_HORSE = optiguiApi.getContainerTexture(Identifier("horse"))!!
private val TEXTURE_INVENTORY = HandledScreen.BACKGROUND_TEXTURE

private val containerFilterCreators = mapOf(
    "anvil" to FilterCreator(Identifier("anvil"), Identifier("chipped_anvil"), Identifier("damaged_anvil")),
    "beacon" to object : FilterCreator(Identifier("beacon")) {
        override fun createFilters(properties: Options, warn: WarnFunction): MutableList<IFilter<Interaction, *>> {
            val levels = properties["levels"]
                ?.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map(NumberOrRange::tryParse) {
                    throwParseException("levels", properties::getValue, "Invalid levels: ${joinNotFound(it)}")
                }
                ?.assertNotEmpty()
                ?: return super.createFilters(properties, warn)

            val filters = super.createFilters(properties, warn)

            filters += PreProcessorFilter.nullGuarded(
                { (it.data as? IBeaconProperties)?.level },
                mismatch(),
                DisjunctionFilter(levels.map { it.toFilter() })
            )

            return filters
        }
    },
    "brewing_stand" to FilterCreator(Identifier("brewing_stand")),
    "chest" to object : FilterCreator(Identifier("chest")) {
        override fun invoke(resource: Identifier, properties: Options, optigui: IOptiGuiExtension) {
            fun String?.parseBoolean() = this?.lowercase()?.trim()?.toBooleanStrict()

            val replacement = resolveReplacementTexture(properties::getValue, resource)
            val large = properties["large"].parseBoolean()
            val trapped = properties["trapped"].parseBoolean()
            val ender = properties["ender"].parseBoolean()
            val christmas = properties["christmas"].parseBoolean()

            if (ender != true) {
                val filters = createFilters(properties, optigui.bindWarnTo(resource))

                val containers = when (trapped) {
                    true -> setOf(Identifier("trapped_chest"))
                    false -> setOf(Identifier("chest"))
                    else -> setOf(Identifier("chest"), Identifier("trapped_chest"))
                }
                filters[0] = PreProcessorFilter(
                    { it.container },
                    ContainingFilter(containers)
                )

                if (large != null) {
                    filters += PreProcessorFilter.nullGuarded(
                        { (it.data as? IChestProperties)?.isLarge },
                        mismatch(),
                        EqualityFilter(large)
                    )
                }

                val christmasFilter = ConjunctionFilter<Interaction>(
                    PreProcessorFilter.nullGuarded(
                        { (it.data as? IIndependentProperties)?.date?.month },
                        mismatch(),
                        EqualityFilter(Month.DECEMBER)
                    ),
                    PreProcessorFilter.nullGuarded(
                        { (it.data as? IIndependentProperties)?.date?.dayOfMonth },
                        mismatch(),
                        RangeFilter.between(24, 26)
                    )
                )
                if (christmas == true) filters.add(christmasFilter)
                if (christmas == false) filters.add(NegationFilter(christmasFilter))

                optigui.addFilter(
                    resource,
                    PostProcessorFilter(ConjunctionFilter(filters), replacement),
                    setOf(TEXTURE_GENERIC_54)
                )
            }
            // Ender chests can't be large, trapped, and can't be named
            if (ender != false && large != true && trapped != true && properties["name"].isNullOrEmpty()) {
                val filters = createFilters(properties, optigui.bindWarnTo(resource))
                filters[0] = PreProcessorFilter(
                    { it.container },
                    EqualityFilter(Identifier("ender_chest"))
                )

                optigui.addFilter(
                    resource,
                    PostProcessorFilter(ConjunctionFilter(filters), replacement),
                    setOf(TEXTURE_GENERIC_54)
                )
            }
        }
    },
    "crafting" to FilterCreator(Identifier("crafting_table")),
    "dispenser" to object : FilterCreator(Identifier("dispenser")) {
        override fun createFilters(properties: Options, warn: WarnFunction): MutableList<IFilter<Interaction, *>> {
            val variants = properties["variants"]
                ?.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map({ it.takeIf { it in dispenserVariants } }) {
                    throwParseException(
                        "variants",
                        properties::getValue,
                        "Invalid dispenser variants: ${joinNotFound(it)}"
                    )
                }
                ?: listOf("dispenser")
            variants.assertNotEmpty()

            val containers = variants.map(::Identifier)
            val filters = super.createFilters(properties, warn)

            filters[0] = PreProcessorFilter(
                { it.container },
                ContainingFilter(containers)
            )

            return filters
        }
    },
    "enchantment" to FilterCreator(Identifier("enchanting_table")),
    "furnace" to FilterCreator(Identifier("furnace")),
    "hopper" to FilterCreator(Identifier("hopper")),
    "horse" to object : FilterCreator(Identifier("horse")) {
        override fun invoke(resource: Identifier, properties: Options, optigui: IOptiGuiExtension) {
            val replacement = resolveReplacementTexture(properties::getValue, resource)
            var variants = properties["variants"]
                ?.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map({ it.takeIf { it in horseVariants } }) {
                    throwParseException("variants", properties::getValue, "Invalid horse variants: ${joinNotFound(it)}")
                }
                ?: throwParseException("variants", properties::getValue, "No horse variants were specified")
            variants = variants.toMutableList()
            variants.assertNotEmpty()

            if (variants.remove("llama")) {
                val filters = createFilters(properties, optigui.bindWarnTo(resource))
                filters[0] = PreProcessorFilter(
                    { it.container },
                    EqualityFilter(Identifier("llama"))
                )

                var colors = properties["colors"]
                    ?.splitIgnoreEmpty(*delimiters)
                    ?.assertNotEmpty()
                if (colors == null) {
                    optigui.addFilter(
                        resource,
                        PostProcessorFilter(ConjunctionFilter(filters), replacement),
                        setOf(TEXTURE_HORSE)
                    )
                } else {
                    colors = colors.map({ it.takeIf { DyeColor.byName(it, null) != null } }) {
                        throwParseException(
                            "colors",
                            properties::getValue,
                            "Invalid carpet colors: ${joinNotFound(it)}"
                        )
                    }.assertNotEmpty()

                    filters += PreProcessorFilter.nullGuarded(
                        { (it.data as? ILlamaProperties)?.carpetColor },
                        mismatch(), // A carpet is required
                        ContainingFilter(colors)
                    )

                    optigui.addFilter(
                        resource,
                        PostProcessorFilter(ConjunctionFilter(filters), replacement),
                        setOf(TEXTURE_HORSE)
                    )
                }
            }

            if (variants.isEmpty()) return

            val filters = createFilters(properties, optigui.bindWarnTo(resource))
            filters[0] = PreProcessorFilter(
                { it.container },
                ContainingFilter(variants.map(::Identifier))
            )

            optigui.addFilter(
                resource,
                PostProcessorFilter(ConjunctionFilter(filters), replacement),
                setOf(TEXTURE_HORSE)
            )
        }
    },
    "villager" to object : FilterCreator(Identifier("villager")) {
        override fun createFilters(properties: Options, warn: WarnFunction): MutableList<IFilter<Interaction, *>> {
            val professions = properties["professions"]
                ?.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map(::tryParseVillagerProfession) {
                    throwParseException("professions", properties::getValue, "Invalid professions: ${joinNotFound(it)}")
                }
                ?.assertNotEmpty()
                ?: return super.createFilters(properties, warn)

            val filters = super.createFilters(properties, warn)

            val profFilters: List<IFilter<Interaction, Unit>> = professions.map { (profession, levels) ->
                if (levels == null) {
                    PreProcessorFilter.nullGuarded(
                        { (it.data as? IVillagerProperties)?.profession },
                        mismatch(),
                        EqualityFilter(profession)
                    )
                } else {
                    ConjunctionFilter(
                        PreProcessorFilter.nullGuarded(
                            { (it.data as? IVillagerProperties)?.profession },
                            mismatch(),
                            EqualityFilter(profession)
                        ),
                        PreProcessorFilter.nullGuarded(
                            { (it.data as? IVillagerProperties)?.level },
                            mismatch(),
                            DisjunctionFilter(levels.map { it.toFilter() })
                        )
                    )
                }
            }
            filters.add(DisjunctionFilter(profFilters))

            return filters
        }
    },
    "shulker_box" to object : FilterCreator(Identifier("shulker_box")) {
        override fun createFilters(properties: Options, warn: WarnFunction): MutableList<IFilter<Interaction, *>> {
            val colors = properties["colors"]
                ?.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map({ it.takeIf { DyeColor.byName(it, null) != null } }) {
                    throwParseException(
                        "colors",
                        properties::getValue,
                        "Invalid shulker box colors: ${joinNotFound(it)}"
                    )
                }
                ?.assertNotEmpty()
                ?: return super.createFilters(properties, warn)

            val blocks = colors.map { color -> "${color}_shulker_box" }
            val filters = super.createFilters(properties, warn)

            filters[0] = PreProcessorFilter(
                { it.container },
                ContainingFilter(blocks.map(::Identifier))
            )

            return filters
        }
    },
    "creative" to fun(_: Identifier, _: Options, _: IOptiGuiExtension) {
        // We're going to pretend that this doesn't exist
    },
    "inventory" to object : FilterCreator(Identifier("player")) {
        override fun invoke(resource: Identifier, properties: Options, optigui: IOptiGuiExtension) {
            val replacement = resolveReplacementTexture(properties::getValue, resource)

            optigui.addFilter(
                resource,
                PostProcessorFilter(
                    ConjunctionFilter(createFilters(properties, optigui.bindWarnTo(resource))),
                    replacement
                ),
                setOf(TEXTURE_INVENTORY)
            )
        }

        override fun createFilters(properties: Options, warn: WarnFunction): MutableList<IFilter<Interaction, *>> {
            val filters = super.createFilters(properties, warn)

            filters[1] = PreProcessorFilter(
                { it.texture },
                EqualityFilter(TEXTURE_INVENTORY)
            )

            return filters
        }
    }
)


private fun tryParseVillagerProfession(prof: String): Pair<Identifier, List<NumberOrRange>?>? {
    return if (':' in prof) {
        if (prof.count { it == ':' } > 2) return null
        val (profName, profLevels) = prof.split(':')
        val profId = Identifier.tryParse(profName) ?: return null
        profId to profLevels.split(',')
            .map unused@{ level -> NumberOrRange.tryParse(level) ?: return null }
    } else {
        (Identifier.tryParse(prof) ?: return null) to null
    }

}

private fun IOptiGuiExtension.bindWarnTo(resource: Identifier): WarnFunction =
    { message -> warn(resource, message) }

private fun <T> wrapParseException(key: String, valueGetter: (String) -> String, block: () -> T): T = try {
    block()
} catch (e: Exception) {
    throw RuntimeException("Failed to parse `$key=${valueGetter(key)}`: ${e.message ?: e}", e)
}

private fun throwParseException(key: String, valueGetter: (String) -> String, message: String): Nothing =
    wrapParseException(key, valueGetter) { throw RuntimeException(message) }

private fun resolveReplacementTexture(
    textureGetter: (String) -> String,
    resourcePath: Identifier,
    textureKey: String = "texture"
): Identifier {
    val texture = textureGetter(textureKey)

    var texturePath = resolvePath(texture, resourcePath, OPTIFINE_TILDE_PATH)
        ?: throwParseException(textureKey, textureGetter, "Resource path cannot be resolved")

    if (resourceAccess.getResource(texturePath).exists()) return texturePath

    texturePath = Identifier(texturePath.namespace, "${texturePath.path}.png")

    return if (resourceAccess.getResource(texturePath).exists()) texturePath
    else throwParseException(textureKey, textureGetter, "Texture doesn't exist")
}
