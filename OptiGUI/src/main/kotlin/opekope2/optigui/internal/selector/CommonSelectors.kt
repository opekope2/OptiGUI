package opekope2.optigui.internal.selector

import net.minecraft.util.Identifier
import net.minecraft.util.Nameable
import opekope2.optigui.filter.ContainingFilter
import opekope2.optigui.filter.DisjunctionFilter
import opekope2.optigui.filter.IFilter.Result.Companion.mismatch
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.filter.RegularExpressionFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.util.buildString
import opekope2.optigui.internal.util.joinNotFound
import opekope2.optigui.selector.ISelector
import opekope2.optigui.util.NumberOrRange
import opekope2.optigui.util.getBiomeId
import org.apache.commons.text.StringEscapeUtils.unescapeJava

internal abstract class AbstractNameSelector : ISelector {
    override fun createFilter(selector: String) = PreProcessorFilter(
        ::getInteractionTargetCustomName,
        "Get custom name of interaction target",
        RegularExpressionFilter(createRegex(selector))
    )

    abstract fun createRegex(selector: String): Regex

    protected fun getInteractionTargetCustomName(interaction: Interaction): String {
        val (_, _, screen, data) = interaction
        return if (data == null) screen.title.string
        else (data.blockEntity as? Nameable ?: data.entityOrRiddenEntity)?.customName?.string ?: screen.title.string
    }

    override fun getRawSelector(interaction: Interaction): String? = null

    protected companion object {
        @JvmStatic
        protected fun getRegexOptions(ignoreCase: Boolean) =
            if (ignoreCase) setOf(RegexOption.IGNORE_CASE)
            else setOf()
    }
}

internal class LiteralNameSelector : AbstractNameSelector() {
    override fun createRegex(selector: String) = Regex(selector, RegexOption.LITERAL)

    override fun getRawSelector(interaction: Interaction) = getInteractionTargetCustomName(interaction)
}

internal class WildcardNameSelector(private val ignoreCase: Boolean) : AbstractNameSelector() {
    override fun createRegex(selector: String) = Regex(
        wildcardToRegex(unescapeJava(selector)),
        getRegexOptions(ignoreCase)
    )

    private fun wildcardToRegex(wildcard: String) = buildString {
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
}

internal class RegexNameSelector(private val ignoreCase: Boolean) : AbstractNameSelector() {
    override fun createRegex(selector: String) = Regex(
        unescapeJava(selector),
        getRegexOptions(ignoreCase)
    )
}

internal class BiomeSelector : AbstractListSelector<Identifier>() {
    override fun parseSelector(selector: String) = Identifier.tryParse(selector)

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid biome identifiers: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<Identifier>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get interaction biome",
        mismatch(),
        ContainingFilter(parsedSelectors)
    )

    override fun transformInteraction(interaction: Interaction): Identifier? {
        val (_, _, _, data) = interaction
        return if (data == null) null
        else (data.blockEntity?.pos ?: data.entityOrRiddenEntity?.blockPos)?.let(data.world::getBiomeId)
    }
}

internal class HeightSelector : AbstractListSelector<NumberOrRange>() {
    override fun parseSelector(selector: String) = NumberOrRange.tryParse(selector)

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid height values: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<NumberOrRange>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get interaction height",
        mismatch(),
        DisjunctionFilter(parsedSelectors.map { it.toFilter() })
    )

    override fun transformInteraction(interaction: Interaction): Int? {
        val (_, _, _, data) = interaction
        return if (data == null) null
        else (data.blockEntity?.pos ?: data.entityOrRiddenEntity?.blockPos)?.y
    }
}
