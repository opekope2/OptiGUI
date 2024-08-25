package opekope2.optigui.filter

import com.google.common.collect.ImmutableList
import java.util.function.BiPredicate
import java.util.function.Predicate

typealias ICollectionOperator<T> = BiPredicate<Collection<Predicate<T>>, T>

/**
 * A collection of multiple sub-filters.
 *
 * @param filters The sub-filter to evaluate
 * @param operator The logical operator to apply between the filter results
 */
class CollectionFilter<TFilter : IFilter<TInput>, TInput>(
    private val filters: ImmutableList<TFilter>,
    private val operator: ICollectionOperator<TInput>
) : IFilter<TInput>, Iterable<TFilter> {
    constructor(filters: Collection<TFilter>, operator: ICollectionOperator<TInput>) :
            this(ImmutableList.copyOf(filters), operator)

    override fun iterator() = filters.iterator()

    override fun test(value: TInput) = operator.test(filters, value)

    @Suppress("UNCHECKED_CAST")
    companion object {
        private val none = ICollectionOperator<Any?> { predicates, testValue -> predicates.none { it.test(testValue) } }
        private val any = ICollectionOperator<Any?> { predicates, testValue -> predicates.any { it.test(testValue) } }
        private val some = ICollectionOperator<Any?> { predicates, testValue -> !predicates.all { it.test(testValue) } }
        private val all = ICollectionOperator<Any?> { predicates, testValue -> predicates.all { it.test(testValue) } }

        /**
         * Matches only if no elements match in the collection. Matches empty collections.
         */
        @JvmStatic
        fun <T> none() = none as ICollectionOperator<T>

        /**
         * Matches if at least 1 element matches in the collection. Doesn't match empty collections.
         */
        @JvmStatic
        fun <T> any() = any as ICollectionOperator<T>

        /**
         * Matches if 0 or more, but not all elements match in the collection. Doesn't match empty collections.
         */
        @JvmStatic
        fun <T> some() = some as ICollectionOperator<T>

        /**
         * Matches if every single element matches in the collection. Matches empty collections.
         */
        @JvmStatic
        fun <T> all() = all as ICollectionOperator<T>
    }
}
