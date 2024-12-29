@file: JvmName("Filters")

package opekope2.optigui.filter

import com.google.common.collect.ImmutableList

/**
 * Matches only if no elements match in the collection. Matches empty collections.
 */
fun <T> matchNone(filters: Collection<IFilter<T>>) = matchNone(ImmutableList.copyOf(filters))

/**
 * Matches only if no elements match in the collection. Matches empty collections.
 */
fun <T> matchNone(filters: ImmutableList<IFilter<T>>) = IFilter<T> {
    filters.none { filter -> filter.test(it) }
}

/**
 * Matches if at least 1 element matches in the collection. Doesn't match empty collections.
 */
fun <T> matchAny(filters: Collection<IFilter<T>>) = matchAny(ImmutableList.copyOf(filters))

/**
 * Matches if at least 1 element matches in the collection. Doesn't match empty collections.
 */
fun <T> matchAny(filters: ImmutableList<IFilter<T>>) = IFilter<T> {
    filters.any { filter -> filter.test(it) }
}

/**
 * Matches if 0 or more, but not all elements match in the collection. Doesn't match empty collections.
 */
fun <T> matchSome(filters: Collection<IFilter<T>>) = matchSome(ImmutableList.copyOf(filters))

/**
 * Matches if 0 or more, but not all elements match in the collection. Doesn't match empty collections.
 */
fun <T> matchSome(filters: ImmutableList<IFilter<T>>) = IFilter<T> {
    !filters.all { filter -> filter.test(it) }
}

/**
 * Matches if every single element matches in the collection. Matches empty collections.
 */
fun <T> matchAll(filters: Collection<IFilter<T>>) = matchAll(ImmutableList.copyOf(filters))

/**
 * Matches if every single element matches in the collection. Matches empty collections.
 */
fun <T> matchAll(filters: ImmutableList<IFilter<T>>) = IFilter<T> {
    filters.all { filter -> filter.test(it) }
}
