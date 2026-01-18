package opekope2.optigui.util

/**
 * An operator describing how to combine boolean values. When a value is [shortCircuitValue], the result of the
 * combination of those values is [shortCircuitResult], or `true`, if none of the values is [shortCircuitValue].
 *
 * @param shortCircuitValue The value after which this operator returns [shortCircuitResult] regardless of any
 *   subsequent values
 * @param shortCircuitResult The result of this operator if any value is [shortCircuitValue]
 */
enum class AggregateOperator(val shortCircuitValue: Boolean, val shortCircuitResult: Boolean) {
    /**
     * An aggregate operator, which requires all boolean values to be `false`.
     */
    NONE(true, false),

    /**
     * An aggregate operator, which requires at least one boolean value to be `true`.
     */
    ANY(true, true),

    /**
     * An aggregate operator, which requires at least one boolean values to be `false`.
     */
    SOME(false, true),

    /**
     * An aggregate operator, which requires all boolean values to be `true`.
     */
    ALL(false, false);

    /**
     * @see shortCircuitValue
     */
    infix fun shortCircuitsOn(value: Boolean) = value == shortCircuitValue
}
