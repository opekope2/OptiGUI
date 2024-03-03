package opekope2.optigui.internal.selector

import opekope2.optigui.filter.IFilter
import opekope2.optigui.interaction.Interaction
import opekope2.optigui.selector.ISelector
import opekope2.optigui.internal.util.assertNotEmpty
import opekope2.optigui.internal.util.delimiters
import opekope2.optigui.internal.util.mapNotNull
import opekope2.optigui.internal.util.splitIgnoreEmpty

internal abstract class AbstractListSelector<T> : ISelector {
    protected abstract fun parseSelector(selector: String): T?

    protected abstract fun parseFailed(invalidSelectors: Collection<String>): Nothing

    protected abstract fun createFilter(parsedSelectors: Collection<T>): IFilter<Interaction, *>

    protected abstract fun transformInteraction(interaction: Interaction): Any?

    final override fun createFilter(selector: String) = selector
        .splitIgnoreEmpty(*delimiters)
        ?.assertNotEmpty()
        ?.mapNotNull(::parseSelector, ::parseFailed)
        ?.assertNotEmpty()
        ?.let { createFilter(it) }

    final override fun getRawSelector(interaction: Interaction): String? = transformInteraction(interaction)?.toString()
}
