package opekope2.filter

import java.util.*

/**
 * Basically a [NullGuardFilter] for Java users using [Optional].
 *
 * @param T The type the given [filter] accepts
 * @param TResult The type [filter] returns
 * @param nullResult The result when the input is not present
 * @param filter The filter to evaluate
 */
class OptionalFilter<T, TResult>(
    private val nullResult: FilterResult<TResult>,
    private val filter: Filter<T, TResult>
) : Filter<Optional<T>, TResult>, Iterable<Filter<T, TResult>> {
    override fun evaluate(value: Optional<T>): FilterResult<out TResult> =
        if (!value.isPresent) nullResult
        else filter.evaluate(value.get())

    override fun iterator(): Iterator<Filter<T, TResult>> = setOf(filter).iterator()

    override fun toString(): String = "${javaClass.name}, result on null: $nullResult"
}
