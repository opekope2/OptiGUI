package opekope2.optigui.filter

import opekope2.optigui.filter.IFilter.Result.*

/**
 * A filter, which returns the opposite result of the provided filter.
 *
 * [IFilter.Result.Match] and [IFilter.Result.Mismatch] are opposites.
 * [IFilter.Result.Skip] is the opposite of itself.
 *
 * @param T The type the filter accepts
 * @param filter The filter to negate
 */
class NegationFilter<T>(private val filter: IFilter<T, *>) : IFilter<T, Unit>, Iterable<IFilter<T, *>> {
    override fun evaluate(value: T): IFilter.Result<out Unit> = when (filter.evaluate(value)) {
        is Match -> Mismatch
        is Mismatch -> Match(Unit)
        is Skip -> Skip
    }

    override fun iterator(): Iterator<IFilter<T, *>> = setOf(filter).iterator()

    override fun toString(): String = javaClass.name
}
