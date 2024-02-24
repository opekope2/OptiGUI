package opekope2.optigui.filter

/**
 * A filter, which only yields a matching result if the given [collection] contains the input.
 * This filter never skips.
 *
 * @param T The type the filter accepts
 * @param collection The collection to check for the input
 */
class ContainingFilter<T>(private val collection: Collection<T>) : IFilter<T, Unit>, Iterable<T> {
    override fun evaluate(value: T): IFilter.Result<out Unit> =
        if (value in collection) IFilter.Result.Match(Unit) else IFilter.Result.Mismatch

    override fun iterator(): Iterator<T> = collection.iterator()

    override fun toString(): String = javaClass.name
}
