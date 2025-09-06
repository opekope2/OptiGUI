package opekope2.optigui.filter

import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString

/**
 * An NBT filter, which tests for the input NBT element's type.
 *
 * @param subFilter The subfilter to test the collection type with
 * @see NbtElement.getType
 */
class NbtTypeFilter(override val subFilter: INbtFilter) : INbtTransformerFilter {
    override fun transform(nbt: NbtElement) = type2name[nbt.type]

    private companion object {
        private val type2name = mapOf(
            NbtElement.END_TYPE to NbtString.of("end"),
            NbtElement.BYTE_TYPE to NbtString.of("byte"),
            NbtElement.SHORT_TYPE to NbtString.of("short"),
            NbtElement.INT_TYPE to NbtString.of("int"),
            NbtElement.LONG_TYPE to NbtString.of("long"),
            NbtElement.FLOAT_TYPE to NbtString.of("float"),
            NbtElement.DOUBLE_TYPE to NbtString.of("double"),
            NbtElement.BYTE_ARRAY_TYPE to NbtString.of("byte_array"),
            NbtElement.STRING_TYPE to NbtString.of("string"),
            NbtElement.LIST_TYPE to NbtString.of("list"),
            NbtElement.COMPOUND_TYPE to NbtString.of("compound"),
            NbtElement.INT_ARRAY_TYPE to NbtString.of("int_array"),
            NbtElement.LONG_ARRAY_TYPE to NbtString.of("long_array"),
        )
    }
}
