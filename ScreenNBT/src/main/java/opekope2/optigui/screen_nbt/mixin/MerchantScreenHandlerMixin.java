package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffers;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_nbt.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MerchantMenu.class)
public abstract class MerchantScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    private MerchantContainer tradeContainer;

    @Shadow
    public abstract boolean canRestock();

    @Shadow
    public abstract int getTraderXp();

    @Shadow
    public abstract int getFutureTraderXp();

    @Shadow
    public abstract int getTraderLevel();

    @Shadow
    public abstract boolean showProgressBar();

    @Shadow
    public abstract MerchantOffers getOffers();

    @Override
    public void optiGui_writeNbt(CompoundTag compound, HolderLookup.Provider lookup) {
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(tradeContainer, lookup));
        compound.putBoolean("can_restock", canRestock());
        compound.putInt("trader_xp", getTraderXp());
        compound.putInt("future_trader_xp", getFutureTraderXp());
        compound.putInt("trader_level", getTraderLevel());
        compound.putBoolean("show_progress_bar", showProgressBar());
        compound.put("offers", NbtUtil.encode(getOffers(), MerchantOffers.CODEC, lookup));
    }
}
