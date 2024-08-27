package opekope2.optigui.internal.filter

import net.minecraft.util.Identifier
import opekope2.optigui.filter.IInteractionFilter
import opekope2.optigui.internal.util.OrderedListLruAccessor

internal class TextureReplacerFilter(
    filter: IInteractionFilter,
    val replacementTexture: Identifier,
    private val priority: Int
) : IInteractionFilter by filter, OrderedListLruAccessor.ValueSupplier {
    override fun getAsInt() = priority
}
