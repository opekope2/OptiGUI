package opekope2.optigui.internal.filter

import net.minecraft.nbt.*
import opekope2.optigui.filter.INbtFilter

internal class NbtCollectionSizeFilter(private val subFilter: INbtFilter) : INbtFilter {
    override fun test(nbt: NbtElement) = when (nbt) {
        is NbtCompound -> subFilter.test(NbtInt.of(nbt.size))
        is AbstractNbtList<*> -> subFilter.test(NbtInt.of(nbt.size))
        is NbtString -> subFilter.test(NbtInt.of(nbt.asString().length))
        else -> false
    }
}
