package opekope2.optigui.filter

/**
 * A filter, which only returns a non-null value, when the input is [expectedValue].
 *
 * @param TInput The type the filter accepts
 * @param expectedValue The value the filter should succeed for
 */
class EqualityFilter<TInput>(private val expectedValue: TInput) : IFilter<TInput, Unit> {
    override fun evaluate(input: TInput): Unit? =
        if (input == expectedValue) Unit
        else null

    override fun toString(): String = "${javaClass.name}, expected value: $expectedValue"
}
