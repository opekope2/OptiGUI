package opekope2.optigui.internal.operator

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import opekope2.optigui.filter.*
import opekope2.optigui.operator.INbtOperator
import opekope2.optigui.resource.format.json.JsonFilterResource
import java.util.function.Function

internal class FilterCollectionOperator(aggregate: Function<Collection<INbtFilter>, INbtFilter>) : INbtOperator {
    private val decoder = Codec.of(null, JsonFilterResource.NBT_FILTER_DECODER).listOf().map(aggregate)

    override fun <T> createFilter(ops: DynamicOps<T>, input: T): DataResult<INbtFilter> = decoder.parse(ops, input)

    companion object {
        @JvmField
        internal val NONE_OF = FilterCollectionOperator(::matchNoneOf)

        @JvmField
        internal val ANY_OF = FilterCollectionOperator(::matchAnyOf)

        @JvmField
        internal val SOME_OF = FilterCollectionOperator(::matchSomeOf)

        @JvmField
        internal val ALL_OF = FilterCollectionOperator(::matchAllOf)
    }
}
