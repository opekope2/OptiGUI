package opekope2.optigui.filter

/**
 * A filter, which returns a non-null value only when the input number is withing the defined range.
 *
 * @see RangeFilter.atLeast
 * @see RangeFilter.atMost
 * @see RangeFilter.between
 */
class RangeFilter private constructor(private val range: IntRange) : IFilter<Int, Unit> {
    override fun evaluate(input: Int) =
        if (input in range) Unit
        else null

    override fun toString() = "${javaClass.name}, range: $range"

    companion object {
        /**
         * Creates a filter, which yields a successful result when the input number >= [min]
         *
         * @param min The inclusive lower bound of the range
         */
        @JvmStatic
        fun atLeast(min: Int) = RangeFilter(min..Int.MAX_VALUE)

        /**
         * Creates a filter, which yields a successful result when the input number <= [max]
         *
         * @param max The inclusive upper bound of the range
         */
        @JvmStatic
        fun atMost(max: Int) = RangeFilter(Int.MIN_VALUE..max)

        /**
         * Creates a filter, which yields a successful result when [min] <= input number <= [max]
         *
         * @param min The inclusive lower bound of the range
         * @param max The inclusive upper bound of the range
         */
        @JvmStatic
        fun between(min: Int, max: Int) = RangeFilter(min..max)
    }
}
