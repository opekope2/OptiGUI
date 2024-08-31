package opekope2.optigui.filter

import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtElement
import opekope2.optigui.mixin.INbtCompoundAccessor
import opekope2.optigui.util.ICollectionOperator

/**
 * A filter filtering an NBT collection.
 *
 * @param filter The sub-filter to evaluate
 * @param operator The logical operator to apply between the filter results
 */
class NbtCollectionFilter(
    private val filter: IFilter<NbtElement>,
    private val operator: ICollectionOperator<NbtElement>
) : IFilter<NbtElement> {
    override fun test(value: NbtElement) = when (value) {
        is INbtCompoundAccessor -> operator.test(filter, value.entries.values)
        is AbstractNbtList<*> -> operator.test(filter, value)
        else -> false
    }
}
