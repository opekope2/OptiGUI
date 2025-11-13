package opekope2.optigui.util.collections

import com.google.common.collect.ImmutableList
import com.mojang.serialization.DataResult

/**
 * Collects the [DataResult] values in the sequence into a list wrapped by the combined [DataResult].
 */
fun <T> Sequence<DataResult<T>>.sequence(): DataResult<List<T>> =
    fold(DataResult.success(ImmutableList.Builder<T>())) { acc, elem ->
        acc.apply2(ImmutableList.Builder<T>::add, elem)
    }.map(ImmutableList.Builder<T>::build)
