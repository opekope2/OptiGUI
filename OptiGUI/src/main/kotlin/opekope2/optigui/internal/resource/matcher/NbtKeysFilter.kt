package opekope2.optigui.internal.resource.matcher

import com.mojang.serialization.Decoder
import net.minecraft.nbt.*
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.resource.format.json.JsonFilterResource

internal class NbtKeysFilter(private val filter: INbtFilter) : INbtFilter {
    override fun test(nbt: NbtElement): Boolean {
        return filter.test(
            when (nbt) {
                is NbtCompound -> nbt.keys.mapTo(NbtList(), NbtString::of)
                is AbstractNbtList<*> -> NbtIntArray(IntArray(nbt.size) { it })
                else -> return false
            }
        )
    }

    companion object {
        @JvmField
        val DECODER: Decoder<INbtFilter> = JsonFilterResource.NBT_FILTER_DECODER.map(::NbtKeysFilter)
    }
}
