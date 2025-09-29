package opekope2.optigui.internal.filter

import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.INbtFilter

internal class NegatedFilter(private val subFilter: INbtFilter) : INbtFilter {
    override fun test(nbt: NbtElement) = !subFilter.test(nbt)
}
