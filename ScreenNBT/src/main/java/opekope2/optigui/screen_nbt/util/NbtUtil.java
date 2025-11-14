package opekope2.optigui.screen_nbt.util;

import com.mojang.serialization.Encoder;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;

public class NbtUtil {
    private NbtUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T> NbtElement encode(T input, Encoder<T> encoder, RegistryWrapper.WrapperLookup lookup) {
        return encoder.encodeStart(lookup.getOps(NbtOps.INSTANCE), input).getOrThrow();
    }

    public static NbtList createInventoryNbt(Inventory inventory, RegistryWrapper.WrapperLookup lookup) {
        NbtList list = new NbtList();
        for (int i = 0; i < inventory.size(); i++) {
            list.add(inventory.getStack(i).encodeAllowEmpty(lookup));
        }
        return list;
    }
}
