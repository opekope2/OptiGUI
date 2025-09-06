package opekope2.optigui.filter

import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtElement

/**
 * A filter filtering for NBT list indices.
 *
 * @param index The index of an NBT list to filter for. Doesn't match if it's outside the list's bounds
 * @param subFilter The filter to evaluate on the list item
 * @see SubNbtFilter
 */
class NbtListIndexFilter(val index: Int, override val subFilter: INbtFilter) : INbtTransformerFilter {
    override fun transform(nbt: NbtElement): NbtElement? = when {
        nbt !is AbstractNbtList<*> -> null
        index in 0 until nbt.size -> nbt[index]
        index in -nbt.size until 0 -> nbt[index + nbt.size]
        else -> null
    }
}
