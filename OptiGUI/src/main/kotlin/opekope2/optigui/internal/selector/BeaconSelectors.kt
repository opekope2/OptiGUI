package opekope2.optigui.internal.selector

import net.minecraft.block.entity.BeaconBlockEntity
import opekope2.optigui.filter.DisjunctionFilter
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.util.joinNotFound
import opekope2.optigui.util.NumberOrRange

internal class BeaconLevelSelector : AbstractListSelector<NumberOrRange>() {
    override fun parseSelector(selector: String) = NumberOrRange.tryParse(selector)

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid beacon levels: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<NumberOrRange>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get beacon level",
        null,
        DisjunctionFilter(parsedSelectors.map { it.toFilter() })
    )

    override fun transformInteraction(interaction: Interaction) =
        (interaction.data.blockEntity as? BeaconBlockEntity)?.level
}
