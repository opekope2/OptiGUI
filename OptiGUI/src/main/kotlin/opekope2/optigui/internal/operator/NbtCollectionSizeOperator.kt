package opekope2.optigui.internal.operator

import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtInt
import net.minecraft.nbt.NbtString
import opekope2.optigui.filter.INbtFilter

internal object NbtCollectionSizeOperator : ISubFilterNbtOperator {
    override fun createFilter(subFilter: INbtFilter) = INbtFilter {
        subFilter.test(
            when (it) {
                is NbtCompound -> NbtInt.of(it.size)
                is AbstractNbtList<*> -> NbtInt.of(it.size)
                is NbtString -> NbtInt.of(it.asString().length)
                else -> return@INbtFilter false
            }
        )
    }
}
