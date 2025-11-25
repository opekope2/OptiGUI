package opekope2.optigui.nbt_provider

import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import opekope2.optigui.filter.transformer.IPrefixNbtTransformer

/**
 * A load-time NBT provider that provides a list of the registered prefix NBT transformer names in
 * [opekope2.optigui.filter.transformer.IPrefixNbtTransformer.Registry].
 */
object PrefixNbtFilterNamesNbtProvider : ILoadTimeNbtProvider {
    override fun get() = IPrefixNbtTransformer.mapTo(ListTag()) { StringTag.valueOf(it.key.toString()) }
}
