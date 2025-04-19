package opekope2.optigui.screen_nbt.util;

import com.mojang.serialization.Encoder;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;

import javax.annotation.Nonnull;

public class NbtUtil {
    private NbtUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T> void encode(NbtCompound nbt, String key, T input, Encoder<T> encoder, RegistryWrapper.WrapperLookup lookup) {
        var ops = lookup.getOps(NbtOps.INSTANCE);
        nbt.put(key, encoder.encodeStart(ops, input).getOrThrow());
    }

    @Nonnull
    public static NbtList createInventoryNbt(Inventory inventory, RegistryWrapper.WrapperLookup lookup) {
        NbtList list = new NbtList();
        for (int i = 0; i < inventory.size(); i++) {
            list.add(inventory.getStack(i).encodeAllowEmpty(lookup));
        }
        return list;
    }
}
