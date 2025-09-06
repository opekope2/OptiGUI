package opekope2.optigui.filter

import com.google.common.collect.Iterators
import net.minecraft.nbt.NbtElement

/**
 * An NBT filter, which negates the output of another filter.
 *
 * @param subFilter The filter to negate the output of
 */
class NegatedFilter(val subFilter: INbtFilter) : INbtFilter, Iterable<INbtFilter> {
    override fun test(nbt: NbtElement) = !subFilter.test(nbt)

    override fun negate() = subFilter

    override fun iterator(): Iterator<INbtFilter> = Iterators.forArray(subFilter)
}
