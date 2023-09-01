package opekope2.optigui.internal.lilac_resource_loader

import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import opekope2.lilac.api.resource.IResourceAccess
import opekope2.lilac.api.resource.IResourceReader
import opekope2.lilac.api.resource.loading.IResourceLoader
import opekope2.lilac.api.resource.loading.IResourceLoadingSession
import opekope2.lilac.util.Util
import opekope2.optigui.api.IOptiGuiApi
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.lilac_resource_loading.IOptiGuiExtension
import opekope2.optigui.filter.*
import opekope2.optigui.properties.*
import opekope2.util.*
import org.apache.commons.text.StringEscapeUtils
import org.ini4j.Ini
import java.time.Month

@Suppress("unused")
class OptiGuiResourceLoader(private val optigui: IOptiGuiExtension) : IResourceLoader {
    override fun getStartingPath(): String = OPTIGUI_RESOURCES_ROOT

    override fun canLoad(resourceName: String): Boolean = resourceName.endsWith(".ini")

    override fun canLoad(resourcePath: Identifier): Boolean =
        resourcePath.namespace == OPTIGUI_NAMESPACE && resourcePath.path.endsWith(".ini")

    override fun loadResource(resource: IResourceReader): Any = resource.id to resource.inputStream.use(::Ini)

    @Suppress("UNCHECKED_CAST")
    override fun processResource(resource: Any) {
        val (resId, ini) = resource as Pair<Identifier, Ini>
        processIni(resId, ini)
    }

    private fun processIni(resource: Identifier, ini: Ini) {
        for ((sectionName, section) in ini) {
            val replacement = (section["replacement"].also {
                if (it == null) optigui.warn(
                    resource,
                    "[$sectionName] Missing `replacement`"
                )
            } ?: continue).let { replace -> resolvePath(replace, resource) }.also {
                if (it == null) optigui.warn(
                    resource,
                    "[$sectionName] Failed to parse `replacement=${section["replacement"]}`: Resource path cannot be resolved"
                )
            } ?: continue
            if (!resourceAccess.getResource(replacement).exists()) {
                optigui.warn(
                    resource,
                    "[$sectionName]: Failed to parse `replacement=${section["replacement"]}`: Texture doesn't exist"
                )
                continue
            }

            val containers =
                sectionName.split(*delimiters).filter { !it.startsWith('#') }.mapNotNull(Identifier::tryParse)

            val sectionFilters = filterCreators.mapNotNull { (name, createFilter) ->
                section[name]?.let { value -> createFilter.createFilter(resource, name, value, optigui) }
            }

            for (container in containers) {
                val replaceableTextures = mutableSetOf(
                    section["interaction.texture"]?.let(Identifier::tryParse)
                        ?: optiguiApi.getContainerTexture(container)
                        ?: continue
                )
                // Because Mojang
                if (minecraft_1_19_4 && container == smithingTable) {
                    replaceableTextures += Identifier("textures/gui/container/legacy_smithing.png")
                }

                val filters = mutableListOf<Filter<Interaction, Unit>>(
                    PreProcessorFilter.nullGuarded(
                        { (it.data as? IGeneralProperties)?.container },
                        FilterResult.Mismatch(),
                        EqualityFilter(container)
                    ),
                    PreProcessorFilter.nullGuarded(
                        { it.texture },
                        FilterResult.Mismatch(),
                        ContainingFilter(replaceableTextures)
                    ),
                )
                filters.addAll(sectionFilters)

                optigui.addFilter(
                    resource,
                    PostProcessorFilter(ConjunctionFilter(filters), replacement),
                    replaceableTextures,
                    section["load.priority"]?.toIntOrNull() ?: 0
                )
            }
        }
    }

    override fun close() {
        optigui.close()
    }

    companion object Factory : IResourceLoader.IFactory {
        override fun createResourceLoader(session: IResourceLoadingSession): IResourceLoader =
            OptiGuiResourceLoader(session["optigui"] as IOptiGuiExtension)
    }
}


private val optiguiApi = IOptiGuiApi.getImplementation()
private val resourceAccess = IResourceAccess.getInstance()
private val minecraft_1_19_4 = Util.checkModVersion("minecraft") { v -> "1.19.4" in v.friendlyString }
private val smithingTable = Identifier("smithing_table")

private abstract class SelectorFilterCreator<T> {
    abstract fun prepareSelector(selectorValue: String): T?

    abstract fun createFilter(preparedSelector: T): Filter<Interaction, Unit>

    fun createFilter(
        resource: Identifier,
        selectorName: String,
        selectorValue: String,
        optigui: IOptiGuiExtension
    ): Filter<Interaction, Unit>? {
        return try {
            createFilter(prepareSelector(selectorValue) ?: return null)
        } catch (e: Exception) {
            optigui.warn(resource, "Failed to parse `$selectorName=$selectorValue`: ${e.message ?: e}")
            null
        }
    }
}

private class SimpleSelectorFilterCreator<T>(
    private val prepareSelector: (String) -> T?,
    private val createFilter: (T) -> Filter<Interaction, Unit>
) : SelectorFilterCreator<T>() {
    override fun prepareSelector(selectorValue: String): T? = prepareSelector.invoke(selectorValue)
    override fun createFilter(preparedSelector: T): Filter<Interaction, Unit> = createFilter.invoke(preparedSelector)
}

private val filterCreators: Map<String, SelectorFilterCreator<*>> = mapOf(
    "name" to SimpleSelectorFilterCreator(
        { name -> Regex.fromLiteral(name) },
        ::createFilterFromName
    ),
    "name.wildcard" to SimpleSelectorFilterCreator(
        { name -> Regex(wildcardToRegex(StringEscapeUtils.unescapeJava(name))) },
        ::createFilterFromName
    ),
    "name.wildcard.ignore_case" to SimpleSelectorFilterCreator(
        { name -> Regex(wildcardToRegex(StringEscapeUtils.unescapeJava(name)), RegexOption.IGNORE_CASE) },
        ::createFilterFromName
    ),
    "name.regex" to SimpleSelectorFilterCreator(
        { name -> Regex(StringEscapeUtils.unescapeJava(name)) },
        ::createFilterFromName
    ),
    "name.regex.ignore_case" to SimpleSelectorFilterCreator(
        { name -> Regex(StringEscapeUtils.unescapeJava(name), RegexOption.IGNORE_CASE) },
        ::createFilterFromName
    ),
    "biomes" to SimpleSelectorFilterCreator(
        { biomes ->
            biomes.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map({ biome -> Identifier.tryParse(biome) }) {
                    throw RuntimeException("Invalid biomes: ${joinNotFound(it)}")
                }
                ?.assertNotEmpty()
        },
        { biomes ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? IGeneralProperties)?.biome },
                FilterResult.Mismatch(),
                ContainingFilter(biomes)
            )
        }
    ),
    "heights" to SimpleSelectorFilterCreator(
        { heights ->
            heights.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map({ height -> NumberOrRange.tryParse(height) }) {
                    throw RuntimeException("Invalid heights: ${joinNotFound(it)}")
                }
                ?.assertNotEmpty()
        },
        { heights ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? IGeneralProperties)?.height },
                FilterResult.Mismatch(),
                DisjunctionFilter(heights.map { it.toFilter() })
            )
        }
    ),
    "date" to SimpleSelectorFilterCreator(
        { dates ->
            dates.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map({ date ->
                    if ('@' in date) {
                        if (date.count { it == '@' } > 2) return@map null
                        val (monthName, day) = date.split('@')
                        val (month) = monthUnmapping.firstOrNull { (_, aliases) -> monthName in aliases }
                            ?: return@map null
                        month to (NumberOrRange.tryParse(day) ?: return@map null)
                    } else {
                        val (month) = monthUnmapping.firstOrNull { (_, aliases) -> date in aliases } ?: return@map null
                        month to null
                    }
                }) { throw RuntimeException("Invalid dates: ${joinNotFound(it)}") }
                ?.assertNotEmpty()
        },
        { dates ->
            DisjunctionFilter(
                dates.map { (month, day) ->
                    val monthFilter = PreProcessorFilter.nullGuarded<Interaction, Month, Unit>(
                        { (it.data as? IIndependentProperties)?.date?.month },
                        FilterResult.Mismatch(),
                        EqualityFilter(month)
                    )
                    val dayFilter = day?.toFilter()

                    if (dayFilter == null) monthFilter
                    else ConjunctionFilter(
                        monthFilter,
                        PreProcessorFilter.nullGuarded(
                            { (it.data as? IIndependentProperties)?.date?.dayOfMonth },
                            FilterResult.Mismatch(),
                            dayFilter
                        )
                    )
                }
            )
        }),
    "comparator.output" to SimpleSelectorFilterCreator(
        { outputs ->
            outputs.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map({ output -> NumberOrRange.tryParse(output) }) {
                    throw RuntimeException("Invalid values: ${joinNotFound(it)}")
                }
                ?.assertNotEmpty()
        },
        { outputs ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? IRedstoneComparatorProperties)?.comparatorOutput },
                FilterResult.Mismatch(),
                DisjunctionFilter(outputs.map { it.toFilter() })
            )
        }
    ),
    "beacon.levels" to SimpleSelectorFilterCreator(
        { levels ->
            levels.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map({ level -> NumberOrRange.tryParse(level) }) {
                    throw RuntimeException("Invalid levels: ${joinNotFound(it)}")
                }
                ?.assertNotEmpty()
        },
        { levels ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? IBeaconProperties)?.level },
                FilterResult.Mismatch(),
                DisjunctionFilter(levels.map { it.toFilter() })
            )
        }
    ),
    "chest.large" to SimpleSelectorFilterCreator(
        { large -> large.toBooleanStrict() },
        { large ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? IChestProperties)?.isLarge },
                FilterResult.Mismatch(),
                EqualityFilter(large)
            )
        }
    ),
    "chest_boat.variants" to SimpleSelectorFilterCreator(
        { variants ->
            variants.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map({ variant -> variant.takeIf { BoatEntity.Type.CODEC.byId(variant) != null } }) {
                    throw RuntimeException("Invalid chest boat variants: ${joinNotFound(it)}")
                }
                ?.assertNotEmpty()
        },
        { variants ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? IChestBoatProperties)?.variant },
                FilterResult.Mismatch(),
                ContainingFilter(variants)
            )
        }
    ),
    "llama.colors" to SimpleSelectorFilterCreator(
        { colors ->
            colors.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map({ color -> color.takeIf { DyeColor.byName(color, null) != null } }) {
                    throw RuntimeException("Invalid colors: ${joinNotFound(it)}")
                }
                ?.assertNotEmpty()
        },
        { variants ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? ILlamaProperties)?.carpetColor },
                FilterResult.Mismatch(), // No carpet is mismatch, because at this point, some colors are required
                ContainingFilter(variants)
            )
        }
    ),
    "villager.professions" to SimpleSelectorFilterCreator(
        { professions ->
            professions.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map({ profession ->
                    if ('@' in profession) {
                        if (profession.count { it == '@' } > 2) return@map null
                        val (profId, profLevel) = profession.split('@')
                        val id = Identifier.tryParse(profId) ?: return@map null
                        val level = NumberOrRange.tryParse(profLevel) ?: return@map null
                        id to level
                    } else {
                        (Identifier.tryParse(profession) ?: return@map null) to null
                    }
                }) { throw RuntimeException("Invalid professions: ${joinNotFound(it)}") }
                ?.assertNotEmpty()
        },
        { professions ->
            DisjunctionFilter(
                professions.map { (profession, level) ->
                    val profFilter = PreProcessorFilter.nullGuarded<Interaction, Identifier, Unit>(
                        { (it.data as? IVillagerProperties)?.profession },
                        FilterResult.Mismatch(),
                        EqualityFilter(profession)
                    )
                    val levelFilter = level?.toFilter()

                    if (levelFilter == null) profFilter
                    else ConjunctionFilter(
                        profFilter,
                        PreProcessorFilter.nullGuarded(
                            { (it.data as? IVillagerProperties)?.level },
                            FilterResult.Mismatch(),
                            levelFilter
                        )
                    )
                }
            )
        }),
    "villager.type" to SimpleSelectorFilterCreator(
        { types ->
            types.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map({ type -> Identifier.tryParse(type) }) {
                    throw RuntimeException("Invalid villager types: ${joinNotFound(it)}")
                }
                ?.assertNotEmpty()
        },
        { types ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? IVillagerProperties)?.type },
                FilterResult.Mismatch(),
                ContainingFilter(types)
            )
        }
    ),
    "interaction.texture" to SimpleSelectorFilterCreator(
        { texture -> Identifier.tryParse(texture) ?: throw RuntimeException("Invalid resource path") },
        { texture ->
            PreProcessorFilter(
                { it.texture },
                EqualityFilter(texture)
            )
        }
    ),
    "donkey.has_chest" to SimpleSelectorFilterCreator(
        { hasChest -> hasChest.toBooleanStrict() },
        { hasChest ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? IDonkeyProperties)?.hasChest },
                FilterResult.Mismatch(),
                EqualityFilter(hasChest)
            )
        }
    ),
    "book.page.current" to SimpleSelectorFilterCreator(
        { currentPage ->
            currentPage.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map({ page -> NumberOrRange.tryParse(page) }) {
                    throw RuntimeException("Invalid page numbers: ${joinNotFound(it)}")
                }
                ?.assertNotEmpty()
        },
        { pages ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? IBookProperties)?.currentPage },
                FilterResult.Mismatch(),
                DisjunctionFilter(pages.map { it.toFilter() })
            )
        }
    ),
    "book.page.count" to SimpleSelectorFilterCreator(
        { pageCount ->
            pageCount.splitIgnoreEmpty(*delimiters)
                ?.assertNotEmpty()
                ?.map({ page -> NumberOrRange.tryParse(page) }) {
                    throw RuntimeException("Invalid page count: ${joinNotFound(it)}")
                }
                ?.assertNotEmpty()
        },
        { pages ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? IBookProperties)?.pageCount },
                FilterResult.Mismatch(),
                DisjunctionFilter(pages.map { it.toFilter() })
            )
        }
    )
)

private val monthUnmapping = arrayOf(
    Month.JANUARY to arrayOf("jan", "january", "1"),
    Month.FEBRUARY to arrayOf("feb", "february", "2"),
    Month.MARCH to arrayOf("mar", "march", "3"),
    Month.APRIL to arrayOf("apr", "april", "4"),
    Month.MAY to arrayOf("may", "5"),
    Month.JUNE to arrayOf("jun", "june", "6"),
    Month.JULY to arrayOf("jul", "july", "7"),
    Month.AUGUST to arrayOf("aug", "augustus", "8"),
    Month.SEPTEMBER to arrayOf("sep", "september", "9"),
    Month.OCTOBER to arrayOf("oct", "october", "spooktober", "10"),
    Month.NOVEMBER to arrayOf("nov", "november", "11"),
    Month.DECEMBER to arrayOf("dec", "december", "12")
)

private fun createFilterFromName(name: Regex): Filter<Interaction, Unit> = PreProcessorFilter(
    { (it.data as? IGeneralProperties)?.name ?: it.screenTitle.string },
    RegularExpressionFilter(name)
)
