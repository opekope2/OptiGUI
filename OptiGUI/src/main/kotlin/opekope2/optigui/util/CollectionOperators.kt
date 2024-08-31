package opekope2.optigui.util

import java.util.function.BiPredicate
import java.util.function.Predicate

/**
 * A function, which decides if a filter matches based on the result of a filters filtering a collection of values.
 *
 * @see PredicateCollectionOperators
 */
typealias ICollectionOperator<T> = BiPredicate<Predicate<T>, Collection<T>>

/**
 * Object holding common [ICollectionOperator]s.
 */
@Suppress("UNCHECKED_CAST")
object CollectionOperators {
    private val none = ICollectionOperator<Any?> { predicate, testValues ->
        testValues.none { predicate.test(it) }
    }
    private val any = ICollectionOperator<Any?> { predicate, testValues ->
        testValues.any { predicate.test(it) }
    }
    private val some = ICollectionOperator<Any?> { predicate, testValues ->
        !testValues.all { predicate.test(it) }
    }
    private val all = ICollectionOperator<Any?> { predicate, testValues ->
        testValues.all { predicate.test(it) }
    }

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
