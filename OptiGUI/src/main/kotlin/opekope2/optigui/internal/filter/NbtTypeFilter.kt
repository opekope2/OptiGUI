package opekope2.optigui.internal.filter

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Decoder
import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.internal.I18n

internal class NbtTypeFilter(private val type: Byte) : INbtFilter {
    override fun test(nbt: NbtElement) = nbt.type == type

    companion object {
        private val name2Type = mapOf(
            "end" to NbtElement.END_TYPE,
            "byte" to NbtElement.BYTE_TYPE,
            "short" to NbtElement.SHORT_TYPE,
            "int" to NbtElement.INT_TYPE,
            "long" to NbtElement.LONG_TYPE,
            "float" to NbtElement.FLOAT_TYPE,
            "double" to NbtElement.DOUBLE_TYPE,
            "byte_array" to NbtElement.BYTE_ARRAY_TYPE,
            "string" to NbtElement.STRING_TYPE,
            "list" to NbtElement.LIST_TYPE,
            "compound" to NbtElement.COMPOUND_TYPE,
            "int_array" to NbtElement.INT_ARRAY_TYPE,
            "long_array" to NbtElement.LONG_ARRAY_TYPE,
        )
        private val type2Name = name2Type.entries.associateBy({ it.value }, { it.key })
        private val stringCodec = Codec.STRING.flatXmap({ map(name2Type, it) }, { map(type2Name, it) })

        @JvmField
        val DECODER: Decoder<NbtTypeFilter> = Codec.withAlternative(Codec.BYTE, stringCodec).map(::NbtTypeFilter)

        private fun <TKey, TValue> map(map: Map<TKey, TValue>, key: TKey): DataResult<TValue> =
            if (key in map) DataResult.success(map[key])
            else DataResult.error { I18n.OPTIGUI_RP_LOADER_ERROR_INVALID_TYPE.getTranslation(key) }
    }
}
