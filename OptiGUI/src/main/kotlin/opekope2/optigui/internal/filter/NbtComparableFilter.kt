package opekope2.optigui.internal.filter

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.*
import net.minecraft.nbt.*
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.internal.I18n
import com.mojang.serialization.Decoder as DfuDecoder

internal sealed class NbtComparableFilter(signBitMask: Int) : INbtFilter {
    private val signBitMask = signBitMask and 0b111

    override fun test(nbt: NbtElement) = compareTo(nbt).matchesBits(signBitMask)

    protected abstract fun compareTo(nbt: NbtElement): Result

    enum class Result(val mask: Int) {
        LESS(1 shl 2),
        EQUAL(1 shl 1),
        MORE(1 shl 0),
        INCOMPARABLE(0);

        fun matchesBits(bits: Int) = ((1 shl ordinal) and bits and 0b111) != 0

        companion object {
            @JvmStatic
            fun ofComparison(result: Int) = when {
                result < 0 -> LESS
                result > 0 -> MORE
                else -> EQUAL
            }
        }
    }

    private class NbtStringFilter(signBitMask: Int, private val reference: String, private val ignoreCase: Boolean) :
        NbtComparableFilter(signBitMask) {
        override fun compareTo(nbt: NbtElement): Result =
            if (nbt is NbtString) Result.ofComparison(reference.compareTo(nbt.asString(), ignoreCase))
            else Result.INCOMPARABLE
    }

    private class NbtIntFilter(signBitMask: Int, private val threshold: Int) : NbtComparableFilter(signBitMask) {
        override fun compareTo(nbt: NbtElement) = when (nbt) {
            is NbtByte, is NbtShort, is NbtInt -> Result.ofComparison(threshold.compareTo(nbt.intValue()))
            is NbtLong -> Result.ofComparison(threshold.compareTo(nbt.longValue()))
            is NbtFloat -> Result.ofComparison(threshold.compareTo(nbt.floatValue()))
            is NbtDouble -> Result.ofComparison(threshold.compareTo(nbt.doubleValue()))
            else -> Result.INCOMPARABLE
        }
    }

    private class NbtLongFilter(signBitMask: Int, private val threshold: Long) : NbtComparableFilter(signBitMask) {
        override fun compareTo(nbt: NbtElement) = when (nbt) {
            is NbtByte, is NbtShort, is NbtInt, is NbtLong -> Result.ofComparison(threshold.compareTo(nbt.longValue()))
            is NbtFloat -> Result.ofComparison(threshold.compareTo(nbt.floatValue()))
            is NbtDouble -> Result.ofComparison(threshold.compareTo(nbt.doubleValue()))
            else -> Result.INCOMPARABLE
        }
    }

    private class NbtFloatFilter(signBitMask: Int, private val threshold: Float) : NbtComparableFilter(signBitMask) {
        override fun compareTo(nbt: NbtElement) = when (nbt) {
            is NbtByte, is NbtShort, is NbtInt, is NbtLong, is NbtFloat -> Result.ofComparison(threshold.compareTo(nbt.floatValue()))
            is NbtDouble -> Result.ofComparison(threshold.compareTo(nbt.doubleValue()))
            else -> Result.INCOMPARABLE
        }
    }

    private class NbtDoubleFilter(signBitMask: Int, private val threshold: Double) : NbtComparableFilter(signBitMask) {
        override fun compareTo(nbt: NbtElement) = when (nbt) {
            is NbtByte, is NbtShort, is NbtInt, is NbtLong, is NbtFloat, is NbtDouble ->
                Result.ofComparison(threshold.compareTo(nbt.doubleValue()))

            else -> Result.INCOMPARABLE
        }
    }

    class Decoder(private val signBitMask: Int, private val ignoreCase: Boolean) : DfuDecoder<NbtComparableFilter> {
        override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<NbtComparableFilter, T>> =
            when (val param = ops.convertTo(JavaOps.INSTANCE, input)) {
                is String -> DataResult.success(NbtStringFilter(signBitMask, param, ignoreCase))
                is Byte, is Short, is Int -> DataResult.success(NbtIntFilter(signBitMask, param.toInt()))
                is Long -> DataResult.success(NbtLongFilter(signBitMask, param))
                is Float -> DataResult.success(NbtFloatFilter(signBitMask, param))
                is Double -> DataResult.success(NbtDoubleFilter(signBitMask, param))
                else -> DataResult.error { I18n.OPTIGUI_RP_LOADER_ERROR_NOT_A_NUMBER_OR_STRING.getTranslation(param) }
            }.map { Pair.of(it, ops.empty()) }
    }

    companion object {
        @JvmField
        val EQUAL_DECODER = Decoder(Result.EQUAL.mask, false)

        @JvmField
        val NON_ENCODING_CODEC: Codec<NbtComparableFilter> = Codec.of(
            Encoder.error(I18n.OPTIGUI_CODEC_ERROR_CANNOT_ENCODE.getTranslation("NbtComparableFilter")),
            EQUAL_DECODER
        )
    }
}
