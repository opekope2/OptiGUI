package opekope2.optigui.internal.operator

import com.mojang.serialization.DataResult
import com.mojang.serialization.Decoder
import com.mojang.serialization.DynamicOps
import net.minecraft.util.dynamic.Codecs
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.internal.filter.*
import opekope2.optigui.operator.INbtOperator
import opekope2.optigui.util.i18n

internal class NbtComparableOperator(signBitMask: Int, ignoreCase: Boolean) : INbtOperator {
    private val decoder: Decoder<INbtFilter> = Codecs.BASIC_OBJECT.flatMap {
        if (ignoreCase && it !is String) DataResult.error {
            i18n("optigui.rp_loader.error.not_a_number_or_string", "Not a number or string: %s", it)
        }
        else when (it) {
            is String -> DataResult.success(NbtStringFilter(signBitMask, it, ignoreCase))
            is Byte, is Short, is Int -> DataResult.success(NbtIntFilter(signBitMask, it.toInt()))
            is Long -> DataResult.success(NbtLongFilter(signBitMask, it))
            is Float -> DataResult.success(NbtFloatFilter(signBitMask, it))
            is Double -> DataResult.success(NbtDoubleFilter(signBitMask, it))
            else -> DataResult.error {
                i18n("optigui.rp_loader.error.not_a_number_or_string", "Not a number or string: %s", it)
            }
        }
    }

    override fun <T> createFilter(ops: DynamicOps<T>, input: T): DataResult<INbtFilter> = decoder.parse(ops, input)
}
