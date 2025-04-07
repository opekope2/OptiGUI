package opekope2.optigui.internal.operator

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.operator.INbtOperator

internal object NbtTypeOperator : INbtOperator {
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
    private val type2Name = name2Type.map { it.value to it.key }.toMap()
    private val STRINGIFIED_CODEC = Codec.STRING.flatXmap({ map(name2Type, it) }, { map(type2Name, it) })
    private val CODEC = Codec.withAlternative(Codec.BYTE, STRINGIFIED_CODEC).map { type ->
        INbtFilter { it.type == type }
    }

    private fun <TKey, TValue> map(map: Map<TKey, TValue>, key: TKey): DataResult<TValue> =
        if (key in map) DataResult.success(map[key])
        else DataResult.error { "Invalid type: $key" }

    override fun <T> createFilter(ops: DynamicOps<T>, input: T): DataResult<INbtFilter> = CODEC.parse(ops, input)
}
