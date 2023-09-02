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
import opekope2.optigui.properties.IBookProperties
import opekope2.util.NumberOrRange
import opekope2.util.delimiters
import opekope2.util.splitIgnoreEmpty


@Selector("book.page.current")
class BookPageSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(NumberOrRange::tryParse) {
                throw RuntimeException("Invalid page numbers: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { pages ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? IBookProperties)?.currentPage },
                    FilterResult.mismatch(),
                    DisjunctionFilter(pages.map { it.toFilter() })
                )
            }
}

@Selector("book.page.count")
class BookPageCountSelector : ISelector {
    override fun createFilter(selector: String): Filter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(NumberOrRange::tryParse) {
                throw RuntimeException("Invalid page count: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { pages ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? IBookProperties)?.pageCount },
                    FilterResult.mismatch(),
                    DisjunctionFilter(pages.map { it.toFilter() })
                )
            }
}
