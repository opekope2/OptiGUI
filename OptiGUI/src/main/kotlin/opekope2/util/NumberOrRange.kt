package opekope2.util

import opekope2.filter.EqualityFilter
import opekope2.filter.Filter
import opekope2.filter.NumberRangeFilter

/**
 * Represents an integer, or a start- and stop-inclusive integer range.
 *
 * @param start The inclusive lower bound of the range or `null`, if this is a number
 * @param end The inclusive upper bound of the range or `null`, if not specified or this is a number
 * @param value The integer value or `null`, if this is a range
 */
class NumberOrRange private constructor(val start: Int?, val end: Int?, val value: Int?) {
    /**
     * Whether or not this object represents an integer.
     */
    val isNumber = value != null

    /**
     * Whether or not this object represents a range.
     */
    val isRange = start != null

    /**
     * Converts the current object to a filter.
     */
    fun toFilter(): Filter<Int, Unit>? =
        if (isRange) {
            if (end == null) NumberRangeFilter.atLeast(start!!) else NumberRangeFilter.between(start!!, end)
        } else if (isNumber) {
            EqualityFilter(value!!)
        } else {
            null
        }

    companion object {
        @JvmStatic
        private val regex = Regex("""^(?:(?<start>\d+|\(-?\d+\))-(?<end>\d+|\(-?\d+\))?|(?<value>-?\d+))$""")

        /**
         * Parses a number or range compatible with [OptiFine docs](https://optifine.readthedocs.io/syntax.html#ranges).
         */
        @JvmStatic
        fun tryParse(numberRange: String): NumberOrRange? {
            val result = regex.matchEntire(numberRange) ?: return null

            val start = result.groups["start"]?.value?.trimParentheses()?.toIntOrNull()
            val end = result.groups["end"]?.value?.trimParentheses()?.toIntOrNull()
            val value = result.groups["value"]?.value?.toIntOrNull()

            return NumberOrRange(start, end, value)
        }
    }
}
