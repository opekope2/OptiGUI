package opekope2.optigui.filter.transformer

import net.minecraft.nbt.NbtElement

/**
 * An NBT transformer, which returns the root NBT.
 */
data object RootNbtTransformer : INbtTransformer {
    override fun transform(nbt: NbtElement, root: NbtElement) = root
}
