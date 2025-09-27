package opekope2.optigui.internal.filter

import com.mojang.serialization.Decoder
import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.INbtFilter
import opekope2.optigui.util.AggregateOperator

class NbtListFilter(private val filter: INbtFilter, private val operator: AggregateOperator) : INbtFilter {
    override fun test(nbt: NbtElement): Boolean {
        if (nbt !is AbstractNbtList<*>) return false
        for (elem in nbt) {
            if (operator shortCircuitsOn filter.test(elem)) return operator.shortCircuitResult
        }
        return true
    }

    companion object {
        fun decoder(operator: AggregateOperator): Decoder<NbtListFilter> =
            INbtFilter.CODEC.map { NbtListFilter(it, operator) }
    }
}
