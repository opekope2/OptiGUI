@file: JvmName("Filters")

package opekope2.optigui.filter

/**
 * Matches only if no filters match in the collection. Matches if [filters][filters] [is empty][isEmpty].
 *
 * @param filters The filters to evaluate
 */
fun <T> matchNoneOf(filters: Collection<IFilter<T>>): IFilter<T> {
    val filterList = filters.toList()
    return IFilter {
        filterList.none { filter -> filter.test(it) }
    }
}

/**
 * Matches if at least 1 filter matches in the collection. Doesn't match if [filters][filters] [is empty][isEmpty].
 *
 * @param filters The filters to evaluate
 */
fun <T> matchAnyOf(filters: Collection<IFilter<T>>): IFilter<T> {
    val filterList = filters.toList()
    return IFilter {
        filterList.any { filter -> filter.test(it) }
    }
}

/**
 * Matches if 0 or more, but not all filters match in the collection. Doesn't match if [filters][filters]
 * [is empty][isEmpty].
 *
 * @param filters The filters to evaluate
 */
fun <T> matchSomeOf(filters: Collection<IFilter<T>>): IFilter<T> {
    val filterList = filters.toList()
    return IFilter {
        !filterList.all { filter -> filter.test(it) }
    }
}

/**
 * Matches if every single filter matches in the collection. Matches if [filters][filters] [is empty][isEmpty].
 *
 * @param filters The filters to evaluate
 */
fun <T> matchAllOf(filters: Collection<IFilter<T>>): IFilter<T> {
    val filterList = filters.toList()
    return IFilter {
        filterList.all { filter -> filter.test(it) }
    }
}
