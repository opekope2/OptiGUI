package opekope2.optigui.filter

/**
 * A filter which applies the logical AND operation between the given filters and returns the result.
 * Only skips if all sub-filters skip, and only yields match if no sub-filters yield mismatch.
 *
 * This filter yields the first non-skipping mismatch result of all sub-filters if any,
 * or the first non-skipping match result of all sub-filters if any,
 * or a new skipping filter result (when all sub-filters skip)
 *
 * @param T The type the filter accepts
 * @param filters The sub-filters to evaluate
 */
class ConjunctiveFilter<T>(private val filters: Iterable<Filter<T>>) : Filter<T>() {
    override fun test(value: T): FilterResult<out T> = filters.map { it.test(value) }.let { results ->
        var ret: FilterResult<out T>? = null // First match

        for (res in results) {
            if (res.skip) continue
            else if (!res.match) return@let res
            else if (ret == null) ret = res
        }

        return@let ret ?: /* all skipped */ FilterResult(skip = true)
    }
}
