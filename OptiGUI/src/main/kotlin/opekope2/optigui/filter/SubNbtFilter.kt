package opekope2.optigui.filter

import net.minecraft.nbt.AbstractNbtList
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement

/**
 * A filter filtering for NBT compound sub-NBTs.
 *
 * [subNbtKey] supports the following values for NBT lists:
 * * `none`: Matches only if no elements match in the list. Matches empty lists
 * * `any`: Matches if at least 1 element matches in the list. Doesn't match empty lists
 * * `some`: Matches if 0 or more, but not all elements match in the list. Doesn't match empty lists
 * * `all`: Matches if every single element matches in the list. Matches empty lists
 *
 * @param subNbtKey The key of an NBT compound to filter for
 * @param filter The filter to evaluate on the sub-NBT
 * @see NbtListIndexFilter
 */
class SubNbtFilter(private val subNbtKey: String, private val filter: INbtFilter) : INbtFilter {
    override fun test(nbt: NbtElement?) = when {
        nbt is NbtCompound && subNbtKey in nbt -> filter.test(nbt[subNbtKey])
        nbt is AbstractNbtList<*> && subNbtKey == "none" -> nbt.none(filter::test)
        nbt is AbstractNbtList<*> && subNbtKey == "any" -> nbt.any(filter::test)
        nbt is AbstractNbtList<*> && subNbtKey == "some" -> !nbt.all(filter::test)
        nbt is AbstractNbtList<*> && subNbtKey == "all" -> nbt.all(filter::test)
        else -> false
    }
}
