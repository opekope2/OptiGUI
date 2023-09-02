package opekope2.optigui.internal.selector

import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.DisjunctionFilter
import opekope2.optigui.filter.Filter
import opekope2.optigui.filter.FilterResult
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.util.assertNotEmpty
import opekope2.util.joinNotFound
import opekope2.util.map
import opekope2.optigui.properties.IRedstoneComparatorProperties
import opekope2.util.NumberOrRange
import opekope2.util.delimiters
import opekope2.util.splitIgnoreEmpty


@Selector("comparator.output")
class RedstoneComparatorOutputSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(NumberOrRange::tryParse) {
                throw RuntimeException("Invalid values: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { outputs ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? IRedstoneComparatorProperties)?.comparatorOutput },
                    FilterResult.mismatch(),
                    DisjunctionFilter(outputs.map { it.toFilter() })
                )
            }
}
