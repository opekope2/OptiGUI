package opekope2.optigui.util

import net.minecraft.nbt.NbtElement
import opekope2.optigui.filter.INbtFilter

/**
 * Stores a filter and arguments to invoke [INbtFilter.test].
 *
 * @param filter The NBT filter to invoke [INbtFilter.test] on
 * @param nbt The first argument for [INbtFilter.test] or `null`, if no NBT can be passed to the filter
 * @param root The second argument for [INbtFilter.test]
 */
data class NbtFilterEvaluation(val filter: INbtFilter, val nbt: NbtElement?, val root: NbtElement) {
    /**
     * Tests [filter] with the specified arguments.
     *
     * @see INbtFilter.test
     */
    fun test() = if (nbt != null) filter.test(nbt, root) else false

    /**
     * Tests [filter]'s subfilters with the specified arguments.
     *
     * @see INbtFilter.testSubFilters
     */
    fun testSubFilters() = filter.testSubFilters(nbt, root)
}
