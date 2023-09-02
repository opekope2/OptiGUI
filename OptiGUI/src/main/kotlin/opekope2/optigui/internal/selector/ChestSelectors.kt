package opekope2.optigui.internal.selector

import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.EqualityFilter
import opekope2.optigui.filter.IFilter
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.properties.IChestProperties


@Selector("chest.large")
class LargeChestSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *> =
        PreProcessorFilter.nullGuarded(
            { (it.data as? IChestProperties)?.isLarge },
            IFilter.Result.mismatch(),
            EqualityFilter(selector.toBooleanStrict())
        )
}
