package opekope2.filter

/**
 * A filter, which only yields a matching result if the given [collection] contains the input.
 * This filter never skips.
 *
 * @param T The type the filter accepts
 * @param collection The collection to check for the input
 */
class ContainingFilter<T>(private val collection: Collection<T>) : Filter<T, Unit>() {
    override fun test(value: T): FilterResult<out Unit> = FilterResult(skip = false, match = value in collection)
}
