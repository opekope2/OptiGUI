package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.village.MerchantInventory;
import net.minecraft.village.TradeOfferList;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import opekope2.optigui.util.INbtConvertible;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MerchantScreenHandler.class)
public abstract class MerchantScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    private MerchantInventory merchantInventory;

    @Shadow
    public abstract boolean canRefreshTrades();

    @Shadow
    public abstract int getExperience();

    @Shadow
    public abstract int getMerchantRewardedExperience();

    @Shadow
    public abstract int getLevelProgress();

    @Shadow
    public abstract boolean isLeveled();

    @Shadow
    public abstract TradeOfferList getRecipes();

    @Override
    public void optiGui_writeNbt(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(merchantInventory, lookup));
        // Mojmap-ish keys, because it makes more sense
        compound.putBoolean("can_restock", canRefreshTrades());
        compound.putInt("trader_xp", getExperience());
        compound.putInt("future_xp", getMerchantRewardedExperience());
        compound.putInt("trader_level", getLevelProgress());
        compound.putBoolean("show_progress_bar", isLeveled());
        compound.put("offers", NbtUtil.encode(getRecipes(), TradeOfferList.CODEC, lookup));
    }
}
