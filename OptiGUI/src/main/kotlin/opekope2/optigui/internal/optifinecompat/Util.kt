package opekope2.optigui.internal.optifinecompat

import net.minecraft.util.Identifier
import opekope2.filter.*
import opekope2.filter.FilterResult.Mismatch
import opekope2.optifinecompat.properties.GeneralProperties
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.resource.Resource
import opekope2.optigui.service.ResourceResolverService
import opekope2.optigui.service.getService
import opekope2.util.NumberOrRange
import opekope2.util.parseWildcardOrRegex
import opekope2.util.resolvePath
import opekope2.util.splitIgnoreEmpty
import java.io.File

internal inline fun <T> MutableCollection<Filter<Interaction, Unit>>.addForProperty(
    resource: Resource,
    property: String,
    propertyConverter: (String) -> T,
    filterCreator: (T) -> Filter<Interaction, Unit>
) {
    (resource.properties[property] as? String)?.let { propertyConverter(it) }?.also { add(filterCreator(it)) }
}

internal inline fun MutableCollection<Filter<Interaction, Unit>>.addForProperty(
    resource: Resource,
    property: String,
    filterCreator: (String) -> Filter<Interaction, Unit>
) = addForProperty(resource, property, { it }, filterCreator)

internal val delimiters = charArrayOf(' ', '\t')

internal fun createGeneralFilters(resource: Resource, texturePath: Identifier): MutableList<Filter<Interaction, Unit>> {
    val filters = createGeneralFilters(resource)

    filters += PreProcessorFilter(
        { it.texture },
        EqualityFilter(texturePath)
    )

    return filters
}

internal fun createGeneralFilters(resource: Resource): MutableList<Filter<Interaction, Unit>> {
    val filters = mutableListOf<Filter<Interaction, Unit>>()

    filters.addForProperty(resource, "name") { name ->
        PreProcessorFilter(
            { (it.data as? GeneralProperties)?.name ?: it.screenTitle.string },
            RegularExpressionFilter(parseWildcardOrRegex(name))
        )
    }

    filters.addForProperty(
        resource,
        "biomes",
        { it.splitIgnoreEmpty(*delimiters).mapNotNull(Identifier::tryParse) }
    ) { biomes ->
        PreProcessorFilter.nullGuarded(
            { (it.data as? GeneralProperties)?.biome },
            Mismatch(),
            ContainingFilter(biomes)
        )
    }

    filters.addForProperty(
        resource,
        "heights",
        { value -> value.splitIgnoreEmpty(*delimiters).mapNotNull { NumberOrRange.tryParse(it)?.toFilter() } }
    ) { heights ->
        PreProcessorFilter.nullGuarded(
            { (it.data as? GeneralProperties)?.height },
            Mismatch(),
            DisjunctionFilter(heights)
        )
    }

    return filters
}

private val resourceResolver: ResourceResolverService by lazy(::getService)
internal fun findReplacementTexture(resource: Resource, texturePath: String): Identifier? {
    val resFolder = File(resource.id.path).parent.replace('\\', '/')

    return resourceResolver.resolveResource(resolvePath(resFolder, texturePath))
}

internal fun findReplacementTexture(resource: Resource): Identifier? {
    return findReplacementTexture(resource, resource.properties["texture"] as? String ?: return null)
}
