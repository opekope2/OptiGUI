package opekope2.optigui.internal.resource.matcher

import net.minecraft.nbt.*

internal class NbtFloatFilter(signBitMask: Int, private val threshold: Float) : NbtComparableFilter(signBitMask) {
    override fun compareTo(nbt: NbtElement) = when (nbt) {
        !is AbstractNbtNumber -> Result.INCOMPARABLE
        is NbtByte, is NbtShort, is NbtInt, is NbtLong, is NbtFloat -> Result.ofComparison(threshold.compareTo(nbt.floatValue()))
        is NbtDouble -> Result.ofComparison(threshold.compareTo(nbt.doubleValue()))
        else -> Result.INCOMPARABLE
    }
}
