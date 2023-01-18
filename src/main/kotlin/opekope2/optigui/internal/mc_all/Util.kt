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

internal fun createGeneralFilters(
    resource: Resource,
    container: String,
    texturePath: Identifier
): MutableList<Filter<Interaction, Unit>> {
    val filters = createGeneralFilters(resource, container)

    filters += TransformationFilter(
        { it.texture },
        EqualityFilter(texturePath)
    )

    return filters
}

internal fun createGeneralFilters(
    resource: Resource,
    container: String
): MutableList<Filter<Interaction, Unit>> {
    val filters = mutableListOf<Filter<Interaction, Unit>>(
        TransformationFilter(
            { (it.data as? GeneralProperties)?.container },
            EqualityFilter(container) // null != container
        )
    )

    filters.addForProperty(resource, "name") { name ->
        TransformationFilter(
            { (it.data as? GeneralProperties)?.name ?: it.screenTitle.string }, // not null
            RegularExpressionFilter(parseWildcardOrRegex(name))
        )
    }
    filters.addForProperty(
        resource,
        "biomes",
        { it.splitIgnoreEmpty(*delimiters).mapNotNull(Identifier::tryParse) }
    ) { biomes ->
        TransformationFilter(
            { (it.data as? GeneralProperties)?.biome },
            ContainingFilter(biomes) // biomes can't contain null
        )
    }
    filters.addForProperty(
        resource,
        "heights",
        { value -> value.splitIgnoreEmpty(*delimiters).mapNotNull { NumberOrRange.parse(it)?.toFilter() } }
    ) { heights ->
        TransformationFilter(
            { (it.data as? GeneralProperties)?.height },
            NullSafeFilter(
                skipOnNull = false,
                failOnNull = true,
                filter = DisjunctionFilter(heights) // can't process null
            )
        )
    }

    return filters
}
