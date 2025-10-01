package opekope2.optigui.filter.comparer

import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.ConstantNbtComparerFilter
import opekope2.optigui.filter.DynamicNbtComparerFilter
import opekope2.optigui.filter.INbtFilter

/**
 * Functional interface for comparing NBT elements.
 *
 * @see ConstantNbtComparerFilter
 * @see DynamicNbtComparerFilter
 */
fun interface INbtComparer {
    /**
     * Compares two NBT elements.
     *
     * @param nbt The left hand side NBT element that represents the first parameter from [INbtFilter.test]
     * @param reference The right hand side NBT element. See documentation of filters invoking [compare]
     * @see Comparable.compareTo
     * @see java.util.Comparator.compare
     */
    fun compare(nbt: NbtElement, reference: NbtElement): ComparisonResult

    /**
     * The result of a comparison.
     */
    enum class ComparisonResult {
        /**
         * The left hand side value is less than the right hand side value.
         */
        LESS,

        /**
         * The left hand side value is equal to the right hand side value.
         */
        EQUAL,

        /**
         * The left hand side value is more than the right hand side value.
         */
        MORE,

        /**
         * The left hand side value is not comparable to the right hand side value.
         */
        INCOMPARABLE;

        companion object {
            /**
             * Returns the appropriate [ComparisonResult] for a given comparison result.
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
}
