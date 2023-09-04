package opekope2.optigui.internal.selector

import net.minecraft.util.Identifier
import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.*
import opekope2.optigui.properties.IGeneralProperties
import opekope2.util.*
import org.apache.commons.text.StringEscapeUtils.unescapeJava


@Selector("name")
object NameSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *> =
        createFilterFromName(Regex.fromLiteral(selector))
}

@Selector("name.wildcard")
object WildcardNameSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *> =
        createFilterFromName(Regex(wildcardToRegex(unescapeJava(selector))))
}

@Selector("name.wildcard.ignore_case")
object CaseInsensitiveWildcardNameSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *> =
        createFilterFromName(Regex(wildcardToRegex(unescapeJava(selector)), RegexOption.IGNORE_CASE))
}

@Selector("name.regex")
object RegexNameSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *> =
        createFilterFromName(Regex(unescapeJava(selector)))
}

@Selector("name.regex.ignore_case")
object CaseInsensitiveRegexNameSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *> =
        createFilterFromName(Regex(unescapeJava(selector), RegexOption.IGNORE_CASE))
}

@Selector("biomes")
object BiomeSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(Identifier::tryParse) {
                throw RuntimeException("Invalid biomes: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { biomes ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? IGeneralProperties)?.biome },
                    IFilter.Result.mismatch(),
                    ContainingFilter(biomes)
                )
            }
}

@Selector("heights")
object HeightSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(NumberOrRange::tryParse) {
                throw RuntimeException("Invalid heights: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { heights ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? IGeneralProperties)?.height },
                    IFilter.Result.mismatch(),
                    DisjunctionFilter(heights.map { it.toFilter() })
                )
            }
}

private fun createFilterFromName(name: Regex): IFilter<Interaction, Unit> = PreProcessorFilter(
    { (it.data as? IGeneralProperties)?.name ?: it.screenTitle.string },
    RegularExpressionFilter(name)
)
