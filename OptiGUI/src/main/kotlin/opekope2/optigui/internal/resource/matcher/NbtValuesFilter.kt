package opekope2.optigui.internal.resource.matcher

import com.mojang.serialization.Decoder
import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.resource.format.json.JsonFilterResource

internal class NbtValuesFilter(private val filter: INbtFilter) : INbtFilter {
    override fun test(nbt: NbtElement): Boolean {
        return filter.test(
            when (nbt) {
                is NbtCompound -> nbt.keys.mapTo(NbtList(), nbt::get)
                is AbstractNbtList<*> -> nbt.mapTo(NbtList()) { it }
                else -> return false
            }
        )
    }

    companion object {
        @JvmField
        val DECODER: Decoder<INbtFilter> = JsonFilterResource.NBT_FILTER_DECODER.map(::NbtValuesFilter)
    }
}
