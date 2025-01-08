@file: JvmName("Filters")

package opekope2.optigui.filter

/**
 * Matches only if no elements match in the collection. Matches empty collections.
 */
fun <T> matchNone(filters: Collection<IFilter<T>>): IFilter<T> {
    val filterList = filters.toList()
    return IFilter {
        filterList.none { filter -> filter.test(it) }
    }
}

/**
 * Matches if at least 1 element matches in the collection. Doesn't match empty collections.
 */
fun <T> matchAny(filters: Collection<IFilter<T>>): IFilter<T> {
    val filterList = filters.toList()
    return IFilter {
        filterList.any { filter -> filter.test(it) }
    }
}

/**
 * Matches if 0 or more, but not all elements match in the collection. Doesn't match empty collections.
 */
fun <T> matchSome(filters: Collection<IFilter<T>>): IFilter<T> {
    val filterList = filters.toList()
    return IFilter {
        !filterList.all { filter -> filter.test(it) }
    }
}

/**
 * Matches if every single element matches in the collection. Matches empty collections.
 */
fun <T> matchAll(filters: Collection<IFilter<T>>): IFilter<T> {
    val filterList = filters.toList()
    return IFilter {
        filterList.all { filter -> filter.test(it) }
    }
}
