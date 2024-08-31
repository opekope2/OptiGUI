package opekope2.optigui.filter

import com.google.common.collect.ImmutableList
import opekope2.optigui.util.IPredicateCollectionOperator

/**
 * A collection of multiple sub-filters.
 *
 * @param filters The sub-filter to evaluate
 * @param operator The logical operator to apply between the filter results
 */
class CollectionFilter<TFilter : IFilter<TInput>, TInput>(
    private val filters: ImmutableList<TFilter>,
    private val operator: IPredicateCollectionOperator<TInput>
) : IFilter<TInput>, Iterable<TFilter> {
    constructor(filters: Collection<TFilter>, operator: IPredicateCollectionOperator<TInput>) :
            this(ImmutableList.copyOf(filters), operator)

    override fun iterator() = filters.iterator()

    override fun test(value: TInput) = operator.test(filters, value)
}
