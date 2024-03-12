package opekope2.optigui.filter

/**
 * A filter, which forwards evaluation to the given [filter], if the input is not `null`.
 * If the input is `null`, the filter will return [inputNullResult].
 *
 * @param TInput The type the given [filter] accepts. This filter accepts its nullable form
 * @param TResult The type [filter] returns
 * @param inputNullResult The result when the input is `null`
 * @param filter The filter to evaluate
 */
class InputNullGuardFilter<TInput, TResult : Any>(
    private val inputNullResult: TResult?,
    private val filter: IFilter<TInput, TResult>
) : IFilter<TInput?, TResult>, Iterable<IFilter<TInput, TResult>> {
    override fun evaluate(input: TInput?) =
        if (input == null) inputNullResult
        else filter.evaluate(input)

    override fun iterator() = iterator { yield(filter) }

    override fun toString() = "${javaClass.name}, result on null: $inputNullResult"
}
