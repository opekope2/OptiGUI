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
class NameSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *> =
        createFilterFromName(Regex.fromLiteral(selector))
}

@Selector("name.wildcard")
class WildcardNameSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *> =
        createFilterFromName(Regex(wildcardToRegex(unescapeJava(selector))))
}

@Selector("name.wildcard.ignore_case")
class CaseInsensitiveWildcardNameSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *> =
        createFilterFromName(Regex(wildcardToRegex(unescapeJava(selector)), RegexOption.IGNORE_CASE))
}

@Selector("name.regex")
class RegexNameSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *> =
        createFilterFromName(Regex(unescapeJava(selector)))
}

@Selector("name.regex.ignore_case")
class CaseInsensitiveRegexNameSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *> =
        createFilterFromName(Regex(unescapeJava(selector), RegexOption.IGNORE_CASE))
}

@Selector("biomes")
class BiomeSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(Identifier::tryParse) {
                throw RuntimeException("Invalid biomes: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { biomes ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? IGeneralProperties)?.biome },
                    FilterResult.mismatch(),
                    ContainingFilter(biomes)
                )
            }
}

@Selector("heights")
class HeightSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(NumberOrRange::tryParse) {
                throw RuntimeException("Invalid heights: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { heights ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? IGeneralProperties)?.height },
                    FilterResult.mismatch(),
                    DisjunctionFilter(heights.map { it.toFilter() })
                )
            }
}

private fun createFilterFromName(name: Regex): Filter<Interaction, Unit> = PreProcessorFilter(
    { (it.data as? IGeneralProperties)?.name ?: it.screenTitle.string },
    RegularExpressionFilter(name)
)
