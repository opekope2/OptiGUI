package opekope2.optigui.filter.transformer

import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag

/**
 * An NBT transformer, which transforms the input NBT to an [StringTag] describing its type.
 *
 * @see Tag.getId
 */
data object NbtTypeTransformer : INbtTransformer {
    private val type2name = mapOf(
        Tag.TAG_END to StringTag.valueOf("end"),
        Tag.TAG_BYTE to StringTag.valueOf("byte"),
        Tag.TAG_SHORT to StringTag.valueOf("short"),
        Tag.TAG_INT to StringTag.valueOf("int"),
        Tag.TAG_LONG to StringTag.valueOf("long"),
        Tag.TAG_FLOAT to StringTag.valueOf("float"),
        Tag.TAG_DOUBLE to StringTag.valueOf("double"),
        Tag.TAG_BYTE_ARRAY to StringTag.valueOf("byte_array"),
        Tag.TAG_STRING to StringTag.valueOf("string"),
        Tag.TAG_LIST to StringTag.valueOf("list"),
        Tag.TAG_COMPOUND to StringTag.valueOf("compound"),
        Tag.TAG_INT_ARRAY to StringTag.valueOf("int_array"),
        Tag.TAG_LONG_ARRAY to StringTag.valueOf("long_array"),
        Tag.TAG_ANY_NUMERIC to StringTag.valueOf("number"),
    )

    /**
     * Gets the string representation of the given NBT type as an [StringTag].
     *
     * @param type One of the `TAG_` fields in [Tag]
     */
    fun transform(type: Byte) = type2name[type]

    override fun transform(nbt: Tag, root: Tag) = transform(nbt.id)
}
