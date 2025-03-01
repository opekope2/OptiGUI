package opekope2.optigui.internal.filter

import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.internal.operator.NbtComparableOperator

internal abstract class NbtComparableFilter(signBitMask: Int) : INbtFilter {
    private val signBitMask = signBitMask and 0b111

    override fun test(nbt: NbtElement) = compareTo(nbt).matchesBits(signBitMask)

    protected abstract fun compareTo(nbt: NbtElement): Result

    protected enum class Result(val mask: Int) {
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
        val MORE_THAN = NbtComparableOperator(Result.MORE.mask, false)

        @JvmField
        val MORE_THAN_IGNORE_CASE = NbtComparableOperator(Result.MORE.mask, true)

        @JvmField
        val AT_LEAST = NbtComparableOperator(Result.MORE.mask or Result.EQUAL.mask, false)

        @JvmField
        val AT_LEAST_IGNORE_CASE = NbtComparableOperator(Result.MORE.mask or Result.EQUAL.mask, true)

        @JvmField
        val EQUAL_TO = NbtComparableOperator(Result.EQUAL.mask, false)

        @JvmField
        val EQUAL_TO_IGNORE_CASE = NbtComparableOperator(Result.EQUAL.mask, true)

        @JvmField
        val NOT_EQUAL_TO = NbtComparableOperator(Result.MORE.mask or Result.LESS.mask, false)

        @JvmField
        val NOT_EQUAL_TO_IGNORE_CASE = NbtComparableOperator(Result.MORE.mask or Result.LESS.mask, true)

        @JvmField
        val AT_MOST = NbtComparableOperator(Result.EQUAL.mask or Result.LESS.mask, false)

        @JvmField
        val AT_MOST_IGNORE_CASE = NbtComparableOperator(Result.EQUAL.mask or Result.LESS.mask, true)

        @JvmField
        val LESS_THAN = NbtComparableOperator(Result.LESS.mask, false)

        @JvmField
        val LESS_THAN_IGNORE_CASE = NbtComparableOperator(Result.LESS.mask, true)
    }
}
