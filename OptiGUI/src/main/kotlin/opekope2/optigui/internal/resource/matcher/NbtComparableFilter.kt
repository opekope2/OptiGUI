package opekope2.optigui.internal.resource.matcher

import com.mojang.serialization.DataResult
import com.mojang.serialization.Decoder
import net.minecraft.nbt.NbtElement
import net.minecraft.util.dynamic.Codecs
import opekope2.optigui.filter.INbtFilter

internal abstract class NbtComparableFilter(signBitMask: Int) : INbtFilter {
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

    companion object {
        @JvmField
        val MORE_THAN_DECODER = decoder(Result.MORE.mask, false)

        @JvmField
        val MORE_THAN_IGNORE_CASE_DECODER = decoder(Result.MORE.mask, true)

        @JvmField
        val AT_LEAST_DECODER = decoder(Result.MORE.mask or Result.EQUAL.mask, false)

        @JvmField
        val AT_LEAST_IGNORE_CASE_DECODER = decoder(Result.MORE.mask or Result.EQUAL.mask, true)

        @JvmField
        val EQUAL_TO_DECODER = decoder(Result.EQUAL.mask, false)

        @JvmField
        val EQUAL_TO_IGNORE_CASE_DECODER = decoder(Result.EQUAL.mask, true)

        @JvmField
        val NOT_EQUAL_TO_DECODER = decoder(Result.MORE.mask or Result.LESS.mask, false)

        @JvmField
        val NOT_EQUAL_TO_IGNORE_CASE_DECODER = decoder(Result.MORE.mask or Result.LESS.mask, true)

        @JvmField
        val AT_MOST_DECODER = decoder(Result.EQUAL.mask or Result.LESS.mask, false)

        @JvmField
        val AT_MOST_IGNORE_CASE_DECODER = decoder(Result.EQUAL.mask or Result.LESS.mask, true)

        @JvmField
        val LESS_THAN_DECODER = decoder(Result.LESS.mask, false)

        @JvmField
        val LESS_THAN_IGNORE_CASE_DECODER = decoder(Result.LESS.mask, true)

        private fun decoder(signBitMask: Int, ignoreCase: Boolean): Decoder<INbtFilter> = Codecs.BASIC_OBJECT.flatMap {
            when (it) {
                is String -> DataResult.success(NbtStringFilter(signBitMask, it, ignoreCase))
                !is Number -> return@flatMap DataResult.error { "Not a number: $it" }
                is Byte, is Short, is Int -> DataResult.success(NbtIntFilter(signBitMask, it.toInt()))
                is Long -> DataResult.success(NbtLongFilter(signBitMask, it))
                is Float -> DataResult.success(NbtFloatFilter(signBitMask, it))
                is Double -> DataResult.success(NbtDoubleFilter(signBitMask, it))
                else -> return@flatMap DataResult.error { "Unsupported number: $it" }
            }
        }
    }
}
