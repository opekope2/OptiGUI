package opekope2.optigui.internal.operator

import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.operator.INbtOperator
import opekope2.optigui.resource.format.json.JsonFilterResource
import opekope2.optigui.util.unwrap

fun interface ISubFilterNbtOperator : INbtOperator {
    override fun <T> createFilter(ops: DynamicOps<T>, input: T): DataResult<INbtFilter> {
        val subFilter = JsonFilterResource.NBT_FILTER_DECODER.parse(ops, input).unwrap { return it }
        return DataResult.success(createFilter(subFilter))
    }

    fun createFilter(subFilter: INbtFilter): INbtFilter
}
