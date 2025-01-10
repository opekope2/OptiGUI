@file: JvmName("Filters")

package opekope2.optigui.filter

import net.minecraft.nbt.AbstractNbtList

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


/**
 * Matches only if no elements match in the NBT collection. Matches empty collections.
 *
 * @param subFilter The filter to evaluate on each of the NBT collection elements
 */
fun matchNone(subFilter: INbtFilter) = INbtFilter {
    if (it !is AbstractNbtList<*>) false
    else it.none(subFilter::test)
}

/**
 * Matches if at least 1 element matches in the NBT collection. Doesn't match empty collections.
 *
 * @param subFilter The filter to evaluate on each of the NBT collection elements
 */
fun matchAny(subFilter: INbtFilter) = INbtFilter {
    if (it !is AbstractNbtList<*>) false
    else it.any(subFilter::test)
}

/**
 * Matches if 0 or more, but not all elements match in the NBT collection. Doesn't match empty collections.
 *
 * @param subFilter The filter to evaluate on each of the NBT collection elements
 */
fun matchSome(subFilter: INbtFilter) = INbtFilter {
    if (it !is AbstractNbtList<*>) false
    else !it.all(subFilter::test)
}

/**
 * Matches if every single element matches in the NBT collection. Matches empty collections.
 *
 * @param subFilter The filter to evaluate on each of the NBT collection elements
 */
fun matchAll(subFilter: INbtFilter) = INbtFilter {
    if (it !is AbstractNbtList<*>) false
    else it.all(subFilter::test)
}
