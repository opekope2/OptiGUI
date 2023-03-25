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

/**
 * OptiFine-compatible filter chain builder.
 *
 * @param resource The resource to load properties from
 */
class FilterBuilder(private val resource: Resource) {
    private var filters = mutableListOf<Filter<Interaction, Unit>>()
    private var generalFiltersAdded = false

    /**
     * The texture to be replaced must be either of these.
     */
    var replaceableTextures: Set<Identifier> = setOf()

    /**
     * A fluent-style setter for [replaceableTextures] with varargs.
     *
     * @param textures the textures to override [replaceableTextures] with
     */
    fun setReplaceableTextures(vararg textures: Identifier): FilterBuilder {
        replaceableTextures = textures.toSet()
        return this
    }

    /**
     * Adds filters for `name`, `biomes`, and `heights` described in the
     * [OptiGUI docs](https://opekope2.github.io/OptiGUI-Next/format), if found.
     *
     * @param T [Interaction.data] must be an instance of this class to match.
     * Interfaces and inheritance are not supported.
     */
    inline fun <reified T> addGeneralFilters() = addGeneralFilters(T::class.java)

    /**
     * Adds filters for `name`, `biomes`, and `heights` described in the
     * [OptiGUI docs](https://opekope2.github.io/OptiGUI-Next/format), if found.
     *
     * @param interactionDataClass [Interaction.data] must be an instance of this class to match.
     * Interfaces and inheritance are not supported.
     */
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

    /**
     * Adds a filter.
     *
     * @param filter The filter to add
     */
    fun addFilter(filter: Filter<Interaction, Unit>) {
        filters += filter
    }

    /**
     * Adds a filter for a property defined in [Resource.properties] if found, and [propertyConverter] returns a non-null value.
     *
     * @param property The name of the property to add filter for
     * @param propertyConverter The function, which converts the property value.
     * If the desired type is `String`, `{ it }` (Kotlin) or `s -> s` (Java) can be used
     * @param filterCreator The function, which creates a filter based on the converted value
     */
    fun <T> addFilterForProperty(
        property: String,
        propertyConverter: (String) -> T,
        filterCreator: (T) -> Filter<Interaction, Unit>
    ): FilterBuilder {
        (resource.properties[property] as? String)?.let { propertyConverter(it) }?.also { filters += filterCreator(it) }
        return this
    }

    /**
     * Creates a filter based on the previously called methods.
     */
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
        /**
         * Builds a filter.
         *
         * @param resource The resources to pass to the constructor
         * @param configure The function, in which the methods of the builder can be called
         */
        @JvmStatic
        fun build(resource: Resource, configure: FilterBuilder.() -> Unit): Filter<Interaction, Unit> {
            val builder = FilterBuilder(resource)
            builder.configure()
            return builder.build()
        }
    }
}
