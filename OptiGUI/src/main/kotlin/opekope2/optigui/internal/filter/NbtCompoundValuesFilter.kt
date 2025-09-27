package opekope2.optigui.internal.filter

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import opekope2.optigui.filter.INbtFilter

internal class NbtCompoundValuesFilter(private val subFilter: INbtFilter) : INbtFilter {
    override fun test(nbt: NbtElement) =
        if (nbt !is NbtCompound) false
        else subFilter.test(nbt.keys.mapTo(NbtList(), nbt::get))
}
