package opekope2.optigui.filter

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.nbt.*
import net.minecraft.util.dynamic.Codecs
import opekope2.optigui.internal.I18n

/**
 * An NBT filter, which compares an NBT element to a predefined reference value.
 *
 * @param T The type of the reference value.
 * @param resultMask The bit mask of the accepted comparison results
 */
sealed class NbtComparableFilter<T : Comparable<T>>(val resultMask: Int) : INbtFilter {
    /**
     * The reference value to compare NBT elements with.
     */
    abstract val reference: T

    override fun test(nbt: NbtElement) = compareTo(nbt).matchesBits(resultMask)

    /**
     * Compares [nbt] to [reference].
     *
     * @param nbt The NBT element to compare
     */
    abstract fun compareTo(nbt: NbtElement): Result

    /**
     * The result of a comparison.
     */
    enum class Result(val mask: Int) {
        /**
         * The reference value is less than the value it was compared to.
         */
        LESS(1 shl 2),

        /**
         * The reference value is equal to the value it was compared to.
         */
        EQUAL(1 shl 1),

        /**
         * The reference value is more than the value it was compared to.
         */
        MORE(1 shl 0),

        /**
         * The reference value is incomparable to the value it was compared to.
         */
        INCOMPARABLE(0);

        /**
         * Returns if the given bit mash matches this result's [mask].
         *
         * @param bits The bit mask of the accepted results
         */
        fun matchesBits(bits: Int) = ((1 shl ordinal) and bits and 0b111) != 0

        companion object {
            /**
             * Returns the appropriate [Result] for a given comparison result.
             *
             * @param result The comparison result returned by [Comparable.compareTo]
             */
            @JvmStatic
            fun ofComparison(result: Int) = when {
                result < 0 -> LESS
                result > 0 -> MORE
                else -> EQUAL
            }
        }
    }

    /**
     * An NBT filter, which compares an NBT element to a predefined reference string.
     *
     * @param reference The reference string to compare NBT elements to
     * @param ignoreCase Whether to ignore case when comparing strings
     * @param resultMask The bit mask of the accepted comparison results
     */
    class NbtStringFilter(override val reference: String, val ignoreCase: Boolean, resultMask: Int) :
        NbtComparableFilter<String>(resultMask) {
        constructor(reference: String, ignoreCase: Boolean, vararg acceptedResults: Result) :
                this(reference, ignoreCase, getMask(acceptedResults))

        override fun compareTo(nbt: NbtElement): Result =
            if (nbt is NbtString) Result.ofComparison(reference.compareTo(nbt.asString(), ignoreCase))
            else Result.INCOMPARABLE
    }

    /**
     * An NBT filter, which compares an NBT element to a predefined reference integer.
     *
     * @param reference The reference integer to compare NBT elements to
     * @param resultMask The bit mask of the accepted comparison results
     */
    class NbtIntFilter(private val threshold: Int, resultMask: Int) : NbtComparableFilter<Int>(resultMask) {
        constructor(threshold: Int, vararg acceptedResults: Result) : this(threshold, getMask(acceptedResults))

        override val reference: Int // Prevent boxing in compareTo(NbtElement)
            get() = threshold

        override fun compareTo(nbt: NbtElement) = when (nbt) {
            is NbtByte, is NbtShort, is NbtInt -> Result.ofComparison(threshold.compareTo(nbt.intValue()))
            is NbtLong -> Result.ofComparison(threshold.compareTo(nbt.longValue()))
            is NbtFloat -> Result.ofComparison(threshold.compareTo(nbt.floatValue()))
            is NbtDouble -> Result.ofComparison(threshold.compareTo(nbt.doubleValue()))
            else -> Result.INCOMPARABLE
        }
    }

    /**
     * An NBT filter, which compares an NBT element to a predefined reference integer.
     *
     * @param reference The reference integer to compare NBT elements to
     * @param resultMask The bit mask of the accepted comparison results
     */
    class NbtLongFilter(private val threshold: Long, resultMask: Int) : NbtComparableFilter<Long>(resultMask) {
        constructor(threshold: Long, vararg acceptedResults: Result) : this(threshold, getMask(acceptedResults))

        override val reference: Long // Prevent boxing in compareTo(NbtElement)
            get() = threshold

        override fun compareTo(nbt: NbtElement) = when (nbt) {
            is NbtByte, is NbtShort, is NbtInt, is NbtLong -> Result.ofComparison(threshold.compareTo(nbt.longValue()))
            is NbtFloat -> Result.ofComparison(threshold.compareTo(nbt.floatValue()))
            is NbtDouble -> Result.ofComparison(threshold.compareTo(nbt.doubleValue()))
            else -> Result.INCOMPARABLE
        }
    }

    /**
     * An NBT filter, which compares an NBT element to a predefined reference floating point number.
     *
     * @param reference The reference float to compare NBT elements to
     * @param resultMask The bit mask of the accepted comparison results
     */
    class NbtFloatFilter(private val threshold: Float, resultMask: Int) : NbtComparableFilter<Float>(resultMask) {
        constructor(threshold: Float, vararg acceptedResults: Result) : this(threshold, getMask(acceptedResults))

        override val reference: Float // Prevent boxing in compareTo(NbtElement)
            get() = threshold

        override fun compareTo(nbt: NbtElement) = when (nbt) {
            is NbtByte, is NbtShort, is NbtInt, is NbtLong, is NbtFloat -> Result.ofComparison(threshold.compareTo(nbt.floatValue()))
            is NbtDouble -> Result.ofComparison(threshold.compareTo(nbt.doubleValue()))
            else -> Result.INCOMPARABLE
        }
    }

    /**
     * An NBT filter, which compares an NBT element to a predefined reference floating point number.
     *
     * @param reference The reference double to compare NBT elements to
     * @param resultMask The bit mask of the accepted comparison results
     */
    class NbtDoubleFilter(private val threshold: Double, resultMask: Int) : NbtComparableFilter<Double>(resultMask) {
        constructor(threshold: Double, vararg acceptedResults: Result) : this(threshold, getMask(acceptedResults))

        override val reference: Double // Prevent boxing in compareTo(NbtElement)
            get() = threshold

        override fun compareTo(nbt: NbtElement) = when (nbt) {
            is NbtByte, is NbtShort, is NbtInt, is NbtLong, is NbtFloat, is NbtDouble ->
                Result.ofComparison(threshold.compareTo(nbt.doubleValue()))

            else -> Result.INCOMPARABLE
        }
    }

    companion object {
        private fun getMask(results: Array<out Result>) = results.fold(0) { acc, result -> acc or result.mask }

        /**
         * Creates a codec for a [NbtComparableFilter].
         *
         * @param ignoreCase Whether to ignore case when comparing strings. Has no effect for number comparison
         * @param acceptedResults The accepted comparison results
         */
        @JvmStatic
        fun codec(ignoreCase: Boolean, vararg acceptedResults: Result): Codec<NbtComparableFilter<*>> {
            return Codecs.BASIC_OBJECT.comapFlatMap({
                when (it) {
                    is String -> DataResult.success(NbtStringFilter(it, ignoreCase, getMask(acceptedResults)))
                    is Byte, is Short, is Int -> DataResult.success(NbtIntFilter(it.toInt(), getMask(acceptedResults)))
                    is Long -> DataResult.success(NbtLongFilter(it, getMask(acceptedResults)))
                    is Float -> DataResult.success(NbtFloatFilter(it, getMask(acceptedResults)))
                    is Double -> DataResult.success(NbtDoubleFilter(it, getMask(acceptedResults)))
                    else -> DataResult.error(I18n.OPTIGUI_RP_LOADER_ERROR_NOT_A_NUMBER_OR_STRING.supplyTranslation(it))
                }
            }, NbtComparableFilter<*>::reference)
        }
    }
}
