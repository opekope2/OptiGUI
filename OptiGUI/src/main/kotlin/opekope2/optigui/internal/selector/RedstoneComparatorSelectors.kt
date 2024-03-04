package opekope2.optigui.internal.selector

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.LecternScreen
import opekope2.optigui.filter.DisjunctionFilter
import opekope2.optigui.filter.IFilter.Result.Companion.mismatch
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.util.joinNotFound
import opekope2.optigui.util.NumberOrRange
import opekope2.optigui.util.redstoneComparatorOutput

internal class RedstoneComparatorOutputSelector : AbstractListSelector<NumberOrRange>() {
    override fun parseSelector(selector: String) = NumberOrRange.tryParse(selector)

    override fun parseFailed(invalidSelectors: Collection<String>): Nothing =
        throw RuntimeException("Invalid redstone comparator output values: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<NumberOrRange>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get redstone comparator output",
        mismatch(),
        DisjunctionFilter(parsedSelectors.map { it.toFilter() })
    )

    override fun transformInteraction(interaction: Interaction) =
        (interaction.screen as? HandledScreen<*>)?.screenHandler?.redstoneComparatorOutput
            ?: (interaction.screen as? LecternScreen)?.redstoneComparatorOutput
}
