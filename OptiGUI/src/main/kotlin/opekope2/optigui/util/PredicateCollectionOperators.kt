package opekope2.optigui.util

import java.util.function.BiPredicate
import java.util.function.Predicate

/**
 * A function, which decides if a filter matches based on the results of a collection of filters filtering a single value.
 *
 * @see PredicateCollectionOperators
 */
typealias IPredicateCollectionOperator<T> = BiPredicate<Collection<Predicate<T>>, T>

/**
 * Object holding common [IPredicateCollectionOperator]s.
 */
@Suppress("UNCHECKED_CAST")
object PredicateCollectionOperators {
    private val none = IPredicateCollectionOperator<Any?> { predicates, testValue ->
        predicates.none { it.test(testValue) }
    }
    private val any = IPredicateCollectionOperator<Any?> { predicates, testValue ->
        predicates.any { it.test(testValue) }
    }
    private val some = IPredicateCollectionOperator<Any?> { predicates, testValue ->
        !predicates.all { it.test(testValue) }
    }
    private val all = IPredicateCollectionOperator<Any?> { predicates, testValue ->
        predicates.all { it.test(testValue) }
    }

    /**
     * Matches only if no elements match in the collection. Matches empty collections.
     */
    @JvmStatic
    fun <T> none() = none as IPredicateCollectionOperator<T>

    /**
     * Matches if at least 1 element matches in the collection. Doesn't match empty collections.
     */
    @JvmStatic
    fun <T> any() = any as IPredicateCollectionOperator<T>

    /**
     * Matches if 0 or more, but not all elements match in the collection. Doesn't match empty collections.
     */
    @JvmStatic
    fun <T> some() = some as IPredicateCollectionOperator<T>

    /**
     * Matches if every single element matches in the collection. Matches empty collections.
     */
    @JvmStatic
    fun <T> all() = all as IPredicateCollectionOperator<T>
}
