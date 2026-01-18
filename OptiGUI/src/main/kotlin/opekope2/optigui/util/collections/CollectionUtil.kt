package opekope2.optigui.util.collections

import com.google.common.collect.ImmutableList
import com.google.common.collect.Table
import com.mojang.serialization.DataResult

/**
 * Collects the [DataResult] values in the sequence into a list wrapped by the combined [DataResult].
 */
fun <T> Sequence<DataResult<T>>.sequence(): DataResult<List<T>> =
    fold(DataResult.success(ImmutableList.Builder<T>())) { acc, elem ->
        acc.apply2(ImmutableList.Builder<T>::add, elem)
    }.map(ImmutableList.Builder<T>::build)

/**
 * [kotlin.collections.getOrPut] implemented for a [Table].
 *
 * @see kotlin.collections.getOrPut
 * @see Table.get
 * @see Table.put
 */
inline fun <R, C, V> Table<R, C, V>.getOrPut(rowKey: R, columnKey: C, defaultValue: () -> V) =
    when (val value = get(rowKey, columnKey)) {
        null -> {
            val default = defaultValue()
            put(rowKey, columnKey, default)
            default
        }

        else -> value
    }
