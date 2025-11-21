package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LecternScreen.class)
public abstract class LecternScreenMixin extends BookViewScreenMixin {
    protected LecternScreenMixin(Component title) {
        super(title);
    }

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = super.optiGui_asNbt(registryAccess);
        float f = callGetNumPages() > 1 ? getCurrentPage() / (callGetNumPages() - 1.0f) : 1.0f;
        compound.putInt(COMPARATOR_OUTPUT_KEY, Mth.floor(f * 14.0f) + 1);
        return compound;
    }
}
