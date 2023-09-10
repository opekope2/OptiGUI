package opekope2.optigui.internal.selector

import opekope2.optigui.annotation.Selector
import opekope2.optigui.api.interaction.Interaction
import opekope2.optigui.api.selector.ISelector
import opekope2.optigui.filter.DisjunctionFilter
import opekope2.optigui.filter.IFilter
import opekope2.optigui.filter.PreProcessorFilter
import opekope2.optigui.properties.IBookProperties
import opekope2.util.*


@Selector("book.page.current")
object BookPageSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(NumberOrRange::tryParse) {
                throw RuntimeException("Invalid page numbers: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { pages ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? IBookProperties)?.currentPage },
                    IFilter.Result.mismatch(),
                    DisjunctionFilter(pages.map { it.toFilter() })
                )
            }
}

@Selector("book.page.count")
object BookPageCountSelector : ISelector {
    override fun createFilter(selector: String): IFilter<Interaction, *>? =
        selector.splitIgnoreEmpty(*delimiters)
            ?.assertNotEmpty()
            ?.map(NumberOrRange::tryParse) {
                throw RuntimeException("Invalid page count: ${joinNotFound(it)}")
            }
            ?.assertNotEmpty()
            ?.let { pages ->
                PreProcessorFilter.nullGuarded(
                    { (it.data as? IBookProperties)?.pageCount },
                    IFilter.Result.mismatch(),
                    DisjunctionFilter(pages.map { it.toFilter() })
                )
            }
}
