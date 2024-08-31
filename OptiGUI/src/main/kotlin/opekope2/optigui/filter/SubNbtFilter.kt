package opekope2.optigui.filter

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement

/**
 * A filter filtering for NBT compound sub-NBTs.
 *
 * @param subNbtKey The key of an NBT compound to filter for
 * @param filter The filter to evaluate on the sub-NBT
 * @see NbtListIndexFilter
 */
class SubNbtFilter(private val subNbtKey: String, private val filter: INbtFilter) : INbtFilter {
    override fun test(nbt: NbtElement?) =
        if (nbt is NbtCompound && subNbtKey in nbt) filter.test(nbt[subNbtKey])
        else false
}
