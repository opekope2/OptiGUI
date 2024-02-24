package opekope2.optigui.filter

import java.util.*

/**
 * A filter, which forwards evaluation to the given [filter], unless the input is [empty][Optional.isEmpty];
 * in this case, the result will be [nullResult].
 *
 * @param T The type the given [filter] accepts
 * @param TResult The type [filter] returns
 * @param nullResult The result when the input is not present
 * @param filter The filter to evaluate
 */
class OptionalFilter<T, TResult>(
    private val nullResult: IFilter.Result<TResult>,
    private val filter: IFilter<T, TResult>
) : IFilter<Optional<T>, TResult>, Iterable<IFilter<T, TResult>> {
    override fun evaluate(value: Optional<T>): IFilter.Result<out TResult> =
        if (value.isEmpty) nullResult
        else filter.evaluate(value.get())

    override fun iterator(): Iterator<IFilter<T, TResult>> = setOf(filter).iterator()

    override fun toString(): String = "${javaClass.name}, result on not present: $nullResult"
}
