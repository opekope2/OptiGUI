package opekope2.optigui.internal.mc_all

import net.minecraft.util.Identifier
import opekope2.filter.*
import opekope2.filter.FilterResult.Mismatch
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.properties.GeneralProperties
import opekope2.optigui.resource.Resource
import opekope2.util.NumberOrRange
import opekope2.util.parseWildcardOrRegex
import opekope2.util.splitIgnoreEmpty

internal inline fun <T> MutableCollection<Filter<Interaction, Unit>>.addForProperty(
    resource: Resource,
    property: String,
    propertyConverter: (String) -> T,
    filterCreator: (T) -> Filter<Interaction, Unit>
) {
    (resource.properties[property] as? String)?.let { add(filterCreator(propertyConverter(it))) }
}

internal inline fun MutableCollection<Filter<Interaction, Unit>>.addForProperty(
    resource: Resource,
    property: String,
    filterCreator: (String) -> Filter<Interaction, Unit>
) = addForProperty(resource, property, { it }, filterCreator)

internal val delimiters = charArrayOf(' ', '\t')

internal fun createGeneralFilters(
    resource: Resource,
    container: String,
    texturePath: Identifier
): MutableList<Filter<Interaction, Unit>> {
    val filters = createGeneralFilters(resource, container)

    val texturePathFilter = EqualityFilter(texturePath)
    filters += Filter { texturePathFilter.evaluate(it.texture) }

    return filters
}

internal fun createGeneralFilters(
    resource: Resource,
    container: String
): MutableList<Filter<Interaction, Unit>> {
    val containerFilter = EqualityFilter(container)

    val filters = mutableListOf<Filter<Interaction, Unit>>(
        Filter {
            containerFilter.evaluate((it.data as? GeneralProperties)?.container ?: return@Filter Mismatch())
        }
    )

    filters.addForProperty(resource, "name") { name ->
        val nameFilter = RegularExpressionFilter(parseWildcardOrRegex(name))

        Filter {
            nameFilter.evaluate((it.data as? GeneralProperties)?.name ?: it.screenTitle.string)
        }
    }

    filters.addForProperty(
        resource,
        "biomes",
        { it.splitIgnoreEmpty(*delimiters).mapNotNull(Identifier::tryParse) }
    ) { biomes ->
        val biomeFilter = ContainingFilter(biomes)

        Filter {
            biomeFilter.evaluate((it.data as? GeneralProperties)?.biome ?: return@Filter Mismatch())
        }
    }

    filters.addForProperty(
        resource,
        "heights",
        { value -> value.splitIgnoreEmpty(*delimiters).mapNotNull { NumberOrRange.parse(it)?.toFilter() } }
    ) { heights ->
        val heightFilter = DisjunctionFilter(heights)

        Filter {
            heightFilter.evaluate((it.data as? GeneralProperties)?.height ?: return@Filter Mismatch())
        }
    }

    return filters
}
