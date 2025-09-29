package opekope2.optigui.util

import com.google.common.collect.ImmutableList
import com.mojang.serialization.DataResult
import opekope2.optigui.util.DataResultAccumulator.Companion.createCollector
import java.util.function.Function
import java.util.stream.Collector
import java.util.stream.Stream

/**
 * An intermediate accumulation type for [Collector], which accumulates [DataResult]s to a single [DataResult].
 *
 * @see createCollector
 */
class DataResultAccumulator<T> {
    private var builder = DataResult.success(ImmutableList.builder<T>())

    private fun add(value: DataResult<T>) {
        builder = builder.apply2(ImmutableList.Builder<T>::add, value)
    }

    private fun combine(a: DataResultAccumulator<T>): DataResultAccumulator<T> {
        builder = builder.apply2({ a, b -> a.addAll(b.build()) }, a.builder)
        return this
    }

    private fun <TResult> finish(finisher: Function<List<T>, TResult>): DataResult<TResult> =
        builder.map { finisher.apply(it.build()) }

    companion object {
        /**
         * Creates a [Collector] for a [Stream] of [DataResult]s.
         *
         * @param finisher A function combining the [DataResult.result]s into a single object
         */
        @JvmStatic
        fun <TStream, TResult> createCollector(finisher: Function<List<TStream>, TResult>): Collector<DataResult<TStream>, DataResultAccumulator<TStream>, DataResult<TResult>> =
            Collector.of(
                ::DataResultAccumulator,
                { acc, result -> acc.add(result) }, // FIXME kotlin compiler crashes when using method reference
                { acc1, acc2 -> acc1.combine(acc2) }, // FIXME kotlin compiler crashes when using method reference
                { it.finish(finisher) }
            )
    }
}
