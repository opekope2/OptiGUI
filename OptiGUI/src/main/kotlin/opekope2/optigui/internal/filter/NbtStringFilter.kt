package opekope2.optigui.internal.filter

import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString

internal class NbtStringFilter(signBitMask: Int, private val reference: String, private val ignoreCase: Boolean) :
    NbtComparableFilter(signBitMask) {
    override fun compareTo(nbt: NbtElement): Result =
        if (nbt is NbtString) Result.ofComparison(reference.compareTo(nbt.asString(), ignoreCase))
        else Result.INCOMPARABLE
}
