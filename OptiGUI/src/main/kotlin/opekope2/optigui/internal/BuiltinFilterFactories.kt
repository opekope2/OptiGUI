package opekope2.optigui.internal

import net.minecraft.util.Identifier
import opekope2.filter.*
import opekope2.filter.FilterResult.Mismatch
import opekope2.filter.factory.FilterFactoryContext
import opekope2.filter.factory.FilterFactoryResult
import opekope2.optigui.InitializerContext
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.service.OptiGlueService
import opekope2.optigui.properties.*
import opekope2.optigui.service.ResourceAccessService
import opekope2.optigui.service.getService
import opekope2.util.*
import org.apache.commons.text.StringEscapeUtils.unescapeJava
import java.time.Month
import java.time.Month.*

@Suppress("unused")
internal fun initializeFilterFactories(context: InitializerContext) {
    context.registerFilterFactory(::createFilter)
}

private val resourceAccess: ResourceAccessService by lazy(::getService)
private val minecraft_1_19_4: Boolean by lazy { getService<OptiGlueService>().minecraftVersion == "1.19.4" }
private val smithingTable = Identifier("smithing_table")

private fun createFilter(context: FilterFactoryContext): FilterFactoryResult? {
    val filters = mutableListOf<Filter<Interaction, out Identifier>>()
    val replaceableTextures = mutableSetOf<Identifier>()

    for ((sectionName, section) in context.resource.ini) {
        val replacement = (section["replacement"].also {
            if (it == null) context.warn("Ignoring section [$sectionName], because it is missing a replacement texture.")
        } ?: continue).let { replace -> resolvePath(replace, context.resource.id) }.also {
            if (it == null) context.warn("Ignoring section [$sectionName], because replacement texture is malformed.")
        } ?: continue
        if (!resourceAccess.getResource(replacement).exists()) {
            context.warn("Ignoring section [$sectionName], because replacement texture doesn't exist.")
            continue
        }

        val containers = sectionName.split(*delimiters).filter { !it.startsWith('#') }.mapNotNull(Identifier::tryParse)
        val sectionFilters = mutableListOf<Filter<Interaction, Unit>>()

        if (containers.any()) {
            sectionFilters += PreProcessorFilter.nullGuarded(
                { (it.data as? CommonProperties)?.container },
                Mismatch(),
                ContainingFilter(containers)
            )
        }

        filterCreators.forEach { (name, createFilter) ->
            section[name]?.let { createFilter(it) }?.let(sectionFilters::add)
        }

        filters += PostProcessorFilter(ConjunctionFilter(sectionFilters)) { _, result ->
            result.withResult(replacement)
        }

        section["interaction.texture"]?.let(Identifier::tryParse)?.also(replaceableTextures::add) ?: run {
            replaceableTextures.addAll(
                containers.mapNotNull(TexturePath::ofContainer)
            )
            // Because Mojang
            if (minecraft_1_19_4 && smithingTable in containers) {
                replaceableTextures += TexturePath.LEGACY_SMITHING_TABLE
            }
        }
    }

    return when {
        filters.isEmpty() && replaceableTextures.isEmpty() -> {
            context.warn("Ignoring resource, because it seems to be empty.")
            null
        }

        filters.isEmpty() -> {
            context.warn("Ignoring resource, because no filters could be created.")
            null
        }

        replaceableTextures.isEmpty() -> {
            context.warn("Ignoring resource, because no replaceable textures were specified or could be guessed.")
            null
        }

        else -> FilterFactoryResult(FirstMatchFilter(filters), replaceableTextures)
    }
}

private val filterCreators = mapOf(
    "name" to createFilterFromProperty(
        { name -> Regex(name, RegexOption.LITERAL) },
        ::createFilterFromName
    ),
    "name.wildcard" to createFilterFromProperty(
        { wildcard -> Regex(wildcardToRegex(unescapeJava(wildcard))) },
        ::createFilterFromName
    ),
    "name.wildcard.ignore_case" to createFilterFromProperty(
        { wildcard -> Regex(wildcardToRegex(unescapeJava(wildcard)), RegexOption.IGNORE_CASE) },
        ::createFilterFromName
    ),
    "name.regex" to createFilterFromProperty(
        { regex -> Regex(unescapeJava(regex)) },
        ::createFilterFromName
    ),
    "name.regex.ignore_case" to createFilterFromProperty(
        { regex -> Regex(unescapeJava(regex), RegexOption.IGNORE_CASE) },
        ::createFilterFromName
    ),
    "biomes" to createFilterFromProperty(
        { it.splitIgnoreEmpty(*delimiters).mapNotNull(Identifier::tryParse).ifEmpty { null } },
        { biomes ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? CommonProperties)?.biome },
                Mismatch(),
                ContainingFilter(biomes)
            )
        }
    ),
    "heights" to createFilterFromProperty(
        { it.splitIgnoreEmpty(*delimiters).mapNotNull(NumberOrRange::tryParse).ifEmpty { null } },
        { heights ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? CommonProperties)?.height },
                Mismatch(),
                DisjunctionFilter(heights.map { it.toFilter() })
            )
        }
    ),
    "date" to createFilterFromProperty(::convertDate) { dates ->
        DisjunctionFilter(
            dates.map { (month, day) ->
                val monthFilter = PreProcessorFilter.nullGuarded<Interaction, Month, Unit>(
                    { (it.data as? IndependentProperties)?.date?.month },
                    Mismatch(),
                    EqualityFilter(month)
                )
                val dayFilter = day?.toFilter()

                if (dayFilter == null) monthFilter
                else ConjunctionFilter(
                    monthFilter,
                    PreProcessorFilter.nullGuarded(
                        { (it.data as? IndependentProperties)?.date?.dayOfMonth },
                        Mismatch(),
                        dayFilter
                    )
                )
            }
        )
    },
    "beacon.levels" to createFilterFromProperty(
        { it.splitIgnoreEmpty(*delimiters).ifEmpty { null } },
        { levels ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? BeaconProperties)?.level },
                Mismatch(),
                DisjunctionFilter(levels.mapNotNull { NumberOrRange.tryParse(it)?.toFilter() })
            )
        }
    ),
    "chest.large" to createFilterFromProperty(String?::toBoolean) { large ->
        PreProcessorFilter.nullGuarded(
            { (it.data as? ChestProperties)?.isLarge },
            Mismatch(),
            EqualityFilter(large)
        )
    },
    "chest_boat.variants" to createFilterFromProperty(
        { it.splitIgnoreEmpty(*delimiters).ifEmpty { null } },
        { variants ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? ChestBoatProperties)?.variant },
                Mismatch(),
                ContainingFilter(variants)
            )
        }
    ),
    "llama.colors" to createFilterFromProperty(
        { it.splitIgnoreEmpty(*delimiters) },
        { variants ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? LlamaProperties)?.carpetColor },
                Mismatch(), // No carpet is mismatch, because at this point, some colors are required
                ContainingFilter(variants)
            )
        }
    ),
    "villager.professions" to createFilterFromProperty(::convertVillagerProfession) { professions ->
        DisjunctionFilter(
            professions.map { (profession, level) ->
                val profFilter = PreProcessorFilter.nullGuarded<Interaction, Identifier, Unit>(
                    { (it.data as? VillagerProperties)?.profession },
                    Mismatch(),
                    EqualityFilter(profession)
                )
                val levelFilter = level?.toFilter()

                if (levelFilter == null) profFilter
                else ConjunctionFilter(
                    profFilter,
                    PreProcessorFilter.nullGuarded(
                        { (it.data as? VillagerProperties)?.level },
                        Mismatch(),
                        levelFilter
                    )
                )
            }
        )
    },
    "interaction.texture" to createFilterFromProperty(Identifier::tryParse) { texture ->
        PreProcessorFilter(
            { it.texture },
            EqualityFilter(texture)
        )
    }
)

private fun wildcardToRegex(wildcard: String): String = buildString {
    append('^')

    for (char in wildcard) {
        append(
            when (char) {
                '*' -> ".+"
                '?' -> ".*"
                '.' -> "\\."
                '\\' -> "\\\\"
                '+' -> "\\+"
                '^' -> "\\^"
                '$' -> "\\$"
                '[' -> "\\["
                ']' -> "\\]"
                '{' -> "\\{"
                '}' -> "\\}"
                '(' -> "\\("
                ')' -> "\\)"
                '|' -> "\\|"
                '/' -> "\\/"
                else -> char.toString()
            }
        )
    }

    append('$')
}

private val monthUnmapping = arrayOf(
    JANUARY to arrayOf("jan", "january", "1"),
    FEBRUARY to arrayOf("feb", "february", "2"),
    MARCH to arrayOf("mar", "march", "3"),
    APRIL to arrayOf("apr", "april", "4"),
    MAY to arrayOf("may", "5"),
    JUNE to arrayOf("jun", "june", "6"),
    JULY to arrayOf("jul", "july", "7"),
    AUGUST to arrayOf("aug", "augustus", "8"),
    SEPTEMBER to arrayOf("sep", "september", "9"),
    OCTOBER to arrayOf("oct", "october", "spooktober", "10"),
    NOVEMBER to arrayOf("nov", "november", "11"),
    DECEMBER to arrayOf("dec", "december", "12")
)

private fun convertDate(dates: String) = sequence {
    for (date in dates.splitIgnoreEmpty(*delimiters)) {
        val parts = date.split('@')
        if (parts.size > 2) continue

        val monthName = parts.getOrNull(0) ?: continue
        val (month, _) = monthUnmapping.firstOrNull { (_, aliases) -> monthName in aliases } ?: continue
        val day = parts.getOrNull(1)?.let(NumberOrRange::tryParse)

        yield(month to day)
    }
}.toList().ifEmpty { null }

private fun convertVillagerProfession(professions: String) = sequence {
    for (profession in professions.splitIgnoreEmpty(*delimiters)) {
        val parts = profession.split('@')
        if (parts.size > 2) continue

        val profId = Identifier.tryParse(parts.getOrNull(0) ?: continue) ?: continue
        val profLevel = NumberOrRange.tryParse(parts.getOrNull(1) ?: continue)

        yield(profId to profLevel)
    }
}.toList().ifEmpty { null }

private fun <T : Any> createFilterFromProperty(
    propertyConverter: (String) -> T?,
    filterCreator: (T) -> Filter<Interaction, Unit>
): (String) -> Filter<Interaction, Unit>? = lambda@{ filterCreator(propertyConverter(it) ?: return@lambda null) }

private fun createFilterFromName(name: Regex): Filter<Interaction, Unit> {
    return PreProcessorFilter(
        { (it.data as? CommonProperties)?.name ?: it.screenTitle.string },
        RegularExpressionFilter(name)
    )
}
