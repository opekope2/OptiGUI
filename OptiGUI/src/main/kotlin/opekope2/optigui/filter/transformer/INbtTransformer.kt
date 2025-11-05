package opekope2.optigui.filter.transformer

import net.minecraft.nbt.NbtElement

/**
 * Functional interface for transforming NBT elements or extracting child NBT elements.
 */
fun interface INbtTransformer {
    /**
     * Transforms an NBT element.
     *
     * @param nbt The NBT element to transform
     * @param root The root NBT element
     * @return The transformed NBT element or `null`, if it can't be transformed
     */
    fun transform(nbt: NbtElement, root: NbtElement): NbtElement?
}
