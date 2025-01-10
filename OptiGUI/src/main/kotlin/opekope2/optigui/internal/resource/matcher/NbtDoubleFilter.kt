package opekope2.optigui.internal.resource.matcher

import net.minecraft.nbt.*

internal class NbtDoubleFilter(signBitMask: Int, private val threshold: Double) : NbtComparableFilter(signBitMask) {
    override fun compareTo(nbt: NbtElement) = when (nbt) {
        !is AbstractNbtNumber -> Result.INCOMPARABLE
        is NbtByte, is NbtShort, is NbtInt, is NbtLong, is NbtFloat, is NbtDouble ->
            Result.ofComparison(threshold.compareTo(nbt.doubleValue()))

        else -> Result.INCOMPARABLE
    }
}
