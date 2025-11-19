package opekope2.optigui.nbt_provider

import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import opekope2.optigui.filter.INbtFilter

/**
 * A load-time NBT provider that provides a list of the registered filter names in [INbtFilter.Registry].
 */
object NbtFilterNamesNbtProvider : ILoadTimeNbtProvider {
    override fun get() = INbtFilter.Registry.mapTo(ListTag()) { StringTag.valueOf(it.key) }
}
