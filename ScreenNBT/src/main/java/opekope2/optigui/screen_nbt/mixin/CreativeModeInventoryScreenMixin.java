package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import opekope2.optigui.screen_api.util.INbtConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin implements INbtConvertible {
    @Shadow
    private static CreativeModeTab selectedTab;

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);
        var tabId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(selectedTab);
        if (tabId != null) compound.putString("tab", tabId.toString());
        return compound;
    }
}
