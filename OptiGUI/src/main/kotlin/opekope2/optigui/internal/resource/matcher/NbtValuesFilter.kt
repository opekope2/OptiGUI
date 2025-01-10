package opekope2.optigui.internal.resource.matcher

import com.mojang.serialization.Decoder
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.resource.format.json.JsonFilterResource

internal class NbtValuesFilter(private val filter: INbtFilter) : INbtFilter {
    override fun test(nbt: NbtElement) =
        if (nbt !is NbtCompound) false
        else filter.test(nbt.keys.mapTo(NbtList(), nbt::get))

    companion object {
        @JvmField
        val DECODER: Decoder<INbtFilter> = JsonFilterResource.NBT_FILTER_DECODER.map(::NbtValuesFilter)
    }
}
