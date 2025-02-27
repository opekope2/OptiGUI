@file: JvmName("Filters")

package opekope2.optigui.filter

import net.minecraft.nbt.AbstractNbtList

/**
 * Matches only if no filters match in the collection. Matches if [filters][filters] [is empty][isEmpty].
 *
 * @param filters The filters to evaluate
 */
fun matchNoneOf(filters: Collection<INbtFilter>): INbtFilter {
    val filterList = filters.toList()
    return INbtFilter {
        filterList.none { filter -> filter.test(it) }
    }
}

/**
 * Matches if at least 1 filter matches in the collection. Doesn't match if [filters][filters] [is empty][isEmpty].
 *
 * @param filters The filters to evaluate
 */
fun matchAnyOf(filters: Collection<INbtFilter>): INbtFilter {
    val filterList = filters.toList()
    return INbtFilter {
        filterList.any { filter -> filter.test(it) }
    }
}

/**
 * Matches if 0 or more, but not all filters match in the collection. Doesn't match if [filters][filters]
 * [is empty][isEmpty].
 *
 * @param filters The filters to evaluate
 */
fun matchSomeOf(filters: Collection<INbtFilter>): INbtFilter {
    val filterList = filters.toList()
    return INbtFilter {
        !filterList.all { filter -> filter.test(it) }
    }
}

/**
 * Matches if every single filter matches in the collection. Matches if [filters][filters] [is empty][isEmpty].
 *
 * @param filters The filters to evaluate
 */
fun matchAllOf(filters: Collection<INbtFilter>): INbtFilter {
    val filterList = filters.toList()
    return INbtFilter {
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
