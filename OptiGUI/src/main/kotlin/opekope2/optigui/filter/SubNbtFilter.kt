package opekope2.optigui.filter

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement

/**
 * A filter filtering for NBT compound sub-NBTs.
 *
 * @param subNbtKey The key of an NBT compound to filter for
 * @param subFilter The filter to evaluate on the sub-NBT
 * @see NbtListIndexFilter
 */
class SubNbtFilter(val subNbtKey: String, override val subFilter: INbtFilter) : INbtTransformerFilter {
    override fun transform(nbt: NbtElement): NbtElement? =
        if (nbt is NbtCompound && nbt.contains(subNbtKey)) nbt[subNbtKey]!!
        else null
}
