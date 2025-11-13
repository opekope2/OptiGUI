package opekope2.optigui.screen_nbt.util;

import com.mojang.serialization.Encoder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;

public class NbtUtil {
    private NbtUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T> Tag encode(T input, Encoder<T> encoder, HolderLookup.Provider lookup) {
        return encoder.encodeStart(lookup.createSerializationContext(NbtOps.INSTANCE), input).getOrThrow();
    }

    public static ListTag createInventoryNbt(Container inventory, HolderLookup.Provider lookup) {
        ListTag list = new ListTag();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            list.add(inventory.getItem(i).saveOptional(lookup));
        }
        return list;
    }
}
