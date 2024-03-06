package opekope2.optigui.filter

/**
 * A filter, which only returns a non-null value, if the given [collection] contains the input.
 *
 * @param TInput The type the filter accepts
 * @param collection The collection to check for the input
 */
class ContainingFilter<TInput>(private val collection: Collection<TInput>) : IFilter<TInput, Unit>, Iterable<TInput> {
    override fun evaluate(input: TInput): Unit? =
        if (input in collection) Unit
        else null

    override fun iterator() = collection.iterator()

    override fun toString(): String = javaClass.name
}
