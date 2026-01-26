package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import opekope2.optigui.screen_api.util.INbtConvertible;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BeaconScreen.class)
public abstract class BeaconScreenMixin implements INbtConvertible {
    @Shadow
    @Nullable
    Holder<MobEffect> primary;

    @Shadow
    @Nullable
    Holder<MobEffect> secondary;

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);
        if (primary != null) compound.putString("primary", primary.getRegisteredName());
        if (secondary != null) compound.putString("secondary", secondary.getRegisteredName());
        return compound;
    }
}
