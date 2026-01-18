package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignText;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HangingSignEditScreen.class)
public abstract class HangingSignEditScreenMixin extends AbstractSignEditScreenMixin implements INbtConvertible {
    protected HangingSignEditScreenMixin(Component title) {
        super(title);
    }

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);
        compound.put(SCREEN_TEXT_KEY, NbtUtil.encode(getText(), SignText.DIRECT_CODEC, registryAccess));
        return compound;
    }
}
