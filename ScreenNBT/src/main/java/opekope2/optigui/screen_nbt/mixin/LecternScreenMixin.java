package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.LecternMenu;
import opekope2.optigui.screen_api.util.INbtConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LecternScreen.class)
public abstract class LecternScreenMixin extends BookViewScreenMixin {
    protected LecternScreenMixin(Component title) {
        super(title);
    }

    @Shadow
    public abstract LecternMenu getMenu();

    @Override
    public void optiGui_writeNbt(CompoundTag compound, HolderLookup.Provider lookup) {
        super.optiGui_writeNbt(compound, lookup);
        ((INbtConvertible) getMenu()).optiGui_writeNbt(compound, lookup);
        float f = getNumPages() > 1 ? getCurrentPage() / (getNumPages() - 1.0f) : 1.0f;
        compound.putInt(COMPARATOR_OUTPUT_KEY, Mth.floor(f * 14.0f) + 1);
    }
}
