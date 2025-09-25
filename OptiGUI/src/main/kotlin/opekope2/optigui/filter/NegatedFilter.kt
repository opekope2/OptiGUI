package opekope2.optigui.filter

import net.minecraft.nbt.NbtElement
import opekope2.optigui.util.NbtFilterEvaluation

/**
 * An NBT filter, which negates the result of another filter.
 *
 * @param subFilter The filter to negate the result of
 */
class NegatedFilter(val subFilter: INbtFilter) : INbtFilter {
    override val type: INbtFilter.Type<NegatedFilter>
        get() = TYPE

    override fun test(nbt: NbtElement, root: NbtElement) = !subFilter.test(nbt, root)

    override fun testSubFilters(nbt: NbtElement, root: NbtElement) = listOf(NbtFilterEvaluation(subFilter, nbt, root))

    companion object {
        /**
         * A type describing a [NegatedFilter].
         */
        @JvmField
        val TYPE =
            INbtFilter.Type(NegatedFilter::class.java, INbtFilter.codec.xmap(::NegatedFilter, NegatedFilter::subFilter))
    }
}
