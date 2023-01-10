package opekope2.filter

/**
 * A filter, which yields a successful result only when the input number is within the defined range, and never skips.
 *
 * @see [atLeast]
 * @see [atMost]
 * @see [between]
 */
class NumberRangeFilter private constructor(private val min: Int, private val max: Int) : Filter<Int, Unit>() {
    override fun test(value: Int): FilterResult<out Unit> = FilterResult(skip = false, match = value in min..max)

    companion object {
        /**
         * Creates a filter, which yields a successful result when the input number >= [min]
         *
         * @param min The inclusive lower bound of the range
         */
        @JvmStatic
        fun atLeast(min: Int) = NumberRangeFilter(min, Int.MAX_VALUE)

        /**
         * Creates a filter, which yields a successful result when the input number <= [max]
         *
         * @param max The inclusive upper bound of the range
         */
        @JvmStatic
        fun atMost(max: Int) = NumberRangeFilter(Int.MIN_VALUE, max)

        /**
         * Creates a filter, which yields a successful result when [min] <= input number <= [max]
         *
         * @param min The inclusive lower bound of the range
         * @param max The inclusive upper bound of the range
         */
        @JvmStatic
        fun between(min: Int, max: Int) = NumberRangeFilter(min, max)
    }
}
