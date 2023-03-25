package opekope2.optifinecompat

import net.minecraft.util.Identifier
import opekope2.filter.*
import opekope2.optifinecompat.properties.GeneralProperties
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.optifinecompat.delimiters
import opekope2.optigui.resource.Resource
import opekope2.util.NumberOrRange
import opekope2.util.parseWildcardOrRegex
import opekope2.util.splitIgnoreEmpty

class FilterBuilder(private val resource: Resource) {
    private var filters = mutableListOf<Filter<Interaction, Unit>>()
    private var generalFiltersAdded = false

    var replaceableTextures: Set<Identifier> = setOf()

    fun setReplaceableTextures(vararg textures: Identifier): FilterBuilder {
        replaceableTextures = textures.toSet()
        return this
    }

    inline fun <reified T> addGeneralFilters() = addGeneralFilters(T::class.java)

    fun addGeneralFilters(interactionDataClass: Class<*>): FilterBuilder {
        if (generalFiltersAdded) return this
        generalFiltersAdded = true

        addFilter(
            PreProcessorFilter(
                { it.data?.javaClass },
                EqualityFilter(interactionDataClass)
            )
        )
        addFilterForProperty("name", { it }) { name ->
            PreProcessorFilter(
                { (it.data as? GeneralProperties)?.name ?: it.screenTitle.string },
                RegularExpressionFilter(parseWildcardOrRegex(name))
            )
        }
        addFilterForProperty(
            "biomes",
            { it.splitIgnoreEmpty(*delimiters).mapNotNull(Identifier::tryParse) }
        ) { biomes ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? GeneralProperties)?.biome },
                FilterResult.Mismatch(),
                ContainingFilter(biomes)
            )
        }
        addFilterForProperty(
            "heights",
            { value -> value.splitIgnoreEmpty(*delimiters).mapNotNull { NumberOrRange.tryParse(it)?.toFilter() } }
        ) { heights ->
            PreProcessorFilter.nullGuarded(
                { (it.data as? GeneralProperties)?.height },
                FilterResult.Mismatch(),
                DisjunctionFilter(heights)
            )
        }

        return this
    }

    fun addFilter(filter: Filter<Interaction, Unit>) {
        filters += filter
    }

    fun <T> addFilterForProperty(
        property: String,
        propertyConverter: (String) -> T,
        filterCreator: (T) -> Filter<Interaction, Unit>
    ): FilterBuilder {
        (resource.properties[property] as? String)?.let { propertyConverter(it) }?.also { filters += filterCreator(it) }
        return this
    }

    fun build(): Filter<Interaction, Unit> {
        val filters = when (replaceableTextures.size) {
            0 -> mutableListOf<Filter<Interaction, Unit>>()

            1 -> mutableListOf(
                PreProcessorFilter(
                    { it.texture },
                    EqualityFilter(replaceableTextures.first())
                )
            )

            else -> mutableListOf(
                PreProcessorFilter(
                    { it.texture },
                    ContainingFilter(replaceableTextures)
                )
            )
        }

        filters.addAll(this.filters)

        return ConjunctionFilter(filters)
    }

    companion object {
        @JvmStatic
        fun build(resource: Resource, configure: FilterBuilder.() -> Unit): Filter<Interaction, Unit> {
            val builder = FilterBuilder(resource)
            builder.configure()
            return builder.build()
        }
    }
}
