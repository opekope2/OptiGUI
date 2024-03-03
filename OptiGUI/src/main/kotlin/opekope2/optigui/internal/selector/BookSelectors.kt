package opekope2.optigui.internal.selector

import net.minecraft.client.gui.screen.ingame.LecternScreen
import opekope2.optigui.filter.DisjunctionFilter
import opekope2.optigui.filter.IFilter.Result.Companion.mismatch
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.internal.interaction.BookExtraProperties
import opekope2.optigui.internal.util.joinNotFound
import opekope2.optigui.util.NumberOrRange

internal class BookPageSelector : AbstractListSelector<NumberOrRange>() {
    override fun parseSelector(selector: String) = NumberOrRange.tryParse(selector)

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid page numbers: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<NumberOrRange>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get book current page",
        mismatch(),
        DisjunctionFilter(parsedSelectors.map { it.toFilter() })
    )

    override fun transformInteraction(interaction: Interaction) =
        (interaction.data?.extra as? BookExtraProperties)?.currentPage
            ?: (interaction.screen as? LecternScreen)?.pageIndex?.plus(1)
}

internal class BookPageCountSelector : AbstractListSelector<NumberOrRange>() {
    override fun parseSelector(selector: String) = NumberOrRange.tryParse(selector)

    override fun parseFailed(invalidSelectors: Collection<String>) =
        throw RuntimeException("Invalid page count: ${joinNotFound(invalidSelectors)}")

    override fun createFilter(parsedSelectors: Collection<NumberOrRange>) = PreProcessorFilter.nullGuarded(
        ::transformInteraction,
        "Get book page count",
        mismatch(),
        DisjunctionFilter(parsedSelectors.map { it.toFilter() })
    )

    override fun transformInteraction(interaction: Interaction) =
        (interaction.data?.extra as? BookExtraProperties)?.pageCount
            ?: (interaction.screen as? LecternScreen)?.pageCount
}
