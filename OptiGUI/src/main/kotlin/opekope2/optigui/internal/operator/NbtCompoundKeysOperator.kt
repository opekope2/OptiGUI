package opekope2.optigui.internal.operator

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import opekope2.optigui.filter.INbtFilter

internal object NbtCompoundKeysOperator : ISubFilterNbtOperator {
    override fun createFilter(subFilter: INbtFilter) = INbtFilter {
        if (it !is NbtCompound) false
        else subFilter.test(it.keys.mapTo(NbtList(), NbtString::of))
    }
}
