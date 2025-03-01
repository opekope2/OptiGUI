package opekope2.optigui.internal.filter

import net.minecraft.nbt.*

internal class NbtIntFilter(signBitMask: Int, private val threshold: Int) : NbtComparableFilter(signBitMask) {
    override fun compareTo(nbt: NbtElement) = when (nbt) {
        is NbtByte, is NbtShort, is NbtInt -> Result.ofComparison(threshold.compareTo(nbt.intValue()))
        is NbtLong -> Result.ofComparison(threshold.compareTo(nbt.longValue()))
        is NbtFloat -> Result.ofComparison(threshold.compareTo(nbt.floatValue()))
        is NbtDouble -> Result.ofComparison(threshold.compareTo(nbt.doubleValue()))
        else -> Result.INCOMPARABLE
    }
}
