package opekope2.optigui.internal.resource.matcher

import net.minecraft.nbt.*

internal class NbtLongFilter(signBitMask: Int, private val threshold: Long) : NbtComparableFilter(signBitMask) {
    override fun compareTo(nbt: NbtElement) = when (nbt) {
        !is AbstractNbtNumber -> Result.INCOMPARABLE
        is NbtByte, is NbtShort, is NbtInt, is NbtLong -> Result.ofComparison(threshold.compareTo(nbt.longValue()))
        is NbtFloat -> Result.ofComparison(threshold.compareTo(nbt.floatValue()))
        is NbtDouble -> Result.ofComparison(threshold.compareTo(nbt.doubleValue()))
        else -> Result.INCOMPARABLE
    }
}
