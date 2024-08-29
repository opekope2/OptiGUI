package opekope2.optigui.filter

import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtElement

/**
 * A filter filtering for NBT list indices.
 *
 * @param index The index of an NBT list to filter for. Doesn't match if it's outside the list's bounds
 * @param filter The filter to evaluate on the list item
 * @see SubNbtFilter
 */
class NbtListIndexFilter(private val index: Int, private val filter: INbtFilter) : INbtFilter {
    override fun test(nbt: NbtElement?) = when {
        nbt is AbstractNbtList<*> && index in 0 until nbt.size -> filter.test(nbt[index])
        nbt is AbstractNbtList<*> && index in -nbt.size until 0 -> filter.test(nbt[index + nbt.size])
        else -> false
    }
}
