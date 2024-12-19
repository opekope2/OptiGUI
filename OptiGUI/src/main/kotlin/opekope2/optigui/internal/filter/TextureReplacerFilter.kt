package opekope2.optigui.internal.filter

import net.minecraft.util.Identifier
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.internal.util.OrderedListLruAccessor

internal class TextureReplacerFilter(
    filter: INbtFilter,
    val replacementTexture: Identifier,
    private val priority: Int
) : INbtFilter by filter, OrderedListLruAccessor.ValueSupplier {
    override fun getAsInt() = priority
}
