package opekope2.filter

/**
 * A filter, which yields a successful result only when the input number is within the defined range, and never skips.
 *
 * @see NumberRangeFilter.atLeast
 * @see NumberRangeFilter.atMost
 * @see NumberRangeFilter.between
 */
class NumberRangeFilter private constructor(private val min: Int, private val max: Int) : Filter<Int, Unit> {
    override fun evaluate(value: Int): FilterResult<out Unit> =
        if (value in min..max) FilterResult.Match(Unit) else FilterResult.Mismatch()

    override fun toString(): String = "${javaClass.name}, $min..$max"

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
