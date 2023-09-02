package opekope2.optigui.internal.selector

import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.DisjunctionFilter
import opekope2.optigui.filter.IFilter
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.properties.IRedstoneComparatorProperties
import opekope2.util.*


@Selector("comparator.output")
class RedstoneComparatorOutputSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(NumberOrRange::tryParse) {
                throw RuntimeException("Invalid values: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { outputs ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? IRedstoneComparatorProperties)?.comparatorOutput },
                    IFilter.Result.mismatch(),
                    DisjunctionFilter(outputs.map { it.toFilter() })
                )
            }
}
