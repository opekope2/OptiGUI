package opekope2.util

import opekope2.filter.EqualityFilter
import opekope2.filter.Filter
import opekope2.filter.NumberRangeFilter

/**
 * Represents an integer, or a start- and stop-inclusive integer range.
 */
sealed class NumberOrRange {
    /**
     * Converts the current number or range to a filter.
     */
    abstract fun toFilter(): Filter<Int, Unit>

    /**
     * Represents an integral number.
     *
     * @param value The integral value
     */
    class Number(val value: Int) : NumberOrRange() {
        override fun toFilter(): Filter<Int, Unit> = EqualityFilter(value)
        override fun toString(): String = value.toString()
    }

    /**
     * Represents a start- and stop-inclusive integer range.
     *
     * @param start The inclusive lower bound of the range
     * @param end The inclusive upper bound of the range or `null`, if there is no upper bound
     */
    class Range(val start: Int, val end: Int?) : NumberOrRange() {
        override fun toFilter(): Filter<Int, Unit> =
            if (end == null) NumberRangeFilter.atLeast(start)
            else NumberRangeFilter.between(start, end)

        override fun toString(): String = "($start)-($end)"
    }

    companion object {
        @JvmStatic
        private val regex = Regex("""^(?:(?<start>\d+|\(-?\d+\))-(?<end>\d+|\(-?\d+\))?|(?<value>-?\d+))$""")

        /**
         * Parses a number or range according to the [OptiGUI docs](https://opekope2.github.io/OptiGUI-Next/syntax/#ranges).
         */
        @JvmStatic
        fun tryParse(numberRange: String): NumberOrRange? {
            val result = regex.matchEntire(numberRange) ?: return null

            val start = result.groups["start"]?.value?.trimParentheses()?.toIntOrNull()
            val end = result.groups["end"]?.value?.trimParentheses()?.toIntOrNull()
            val value = result.groups["value"]?.value?.toIntOrNull()

            return if (value == null) Range(start!!, end) else Number(value)
        }
    }
}
