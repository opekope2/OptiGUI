package opekope2.optigui.internal.mc_all

import net.minecraft.util.Identifier
import opekope2.filter.*
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

internal const val textureFilterIndex = 1

internal fun createGeneralFilters(
    resource: Resource,
    container: String,
    texturePath: Identifier
): MutableList<Filter<Interaction, Unit>> {
    val filters = mutableListOf<Filter<Interaction, Unit>>(
        TransformationFilter(
            { (it.data as? GeneralProperties)?.container },
            EqualityFilter(container)
        ),
        // textureFilterIndex
        TransformationFilter(
            { it.texture },
            EqualityFilter(texturePath)
        )
    )

    filters.addForProperty(resource, "name") { name ->
        TransformationFilter(
            { (it.data as? GeneralProperties)?.name ?: it.screenTitle.string },
            RegularExpressionFilter(parseWildcardOrRegex(name))
        )
    }
    filters.addForProperty(resource, "biomes") { biomes ->
        TransformationFilter(
            { (it.data as? GeneralProperties)?.biome },
            ContainingFilter(biomes.splitIgnoreEmpty(*delimiters).mapNotNull(Identifier::tryParse))
        )
    }
    filters.addForProperty(resource, "heights") { heights ->
        TransformationFilter(
            { (it.data as? GeneralProperties)?.height },
            NullableFilter(
                skipOnNull = false,
                failOnNull = true,
                filter = DisjunctionFilter(
                    heights.splitIgnoreEmpty(*delimiters).mapNotNull { NumberOrRange.parse(it)?.toFilter() })
            )
        )
    }

    return filters
}
