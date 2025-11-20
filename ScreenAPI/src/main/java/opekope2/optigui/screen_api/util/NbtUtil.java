package opekope2.optigui.screen_api.util;

import com.mojang.serialization.Encoder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;

/**
 * NBT utilities intended to be used by {@link INbtConvertible} implementations.
 */
public class NbtUtil {
    private NbtUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Encodes {@code input} using {@code encoder} as NBT.
     *
     * @param <T>     The type of {@code input}
     * @param input   The data to encode
     * @param encoder The codec used to encode {@code input}
     * @param lookup  The registry lookup used to encode NBT
     */
    public static <T> Tag encode(T input, Encoder<T> encoder, HolderLookup.Provider lookup) {
        return encoder.encodeStart(lookup.createSerializationContext(NbtOps.INSTANCE), input).getOrThrow();
    }

    /**
     * Encodes an inventory contents as NBT.
     * This is not the same structure the game stores the inventory contents, but rather a common inventory NBT format
     * for {@link INbtConvertible} implementations.
     *
     * @param inventory The inventory to encode
     * @param lookup    The registry lookup used to encode NBT
     */
    public static ListTag createInventoryNbt(Container inventory, HolderLookup.Provider lookup) {
        ListTag list = new ListTag();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            list.add(inventory.getItem(i).saveOptional(lookup));
        }
        return list;
    }
}
