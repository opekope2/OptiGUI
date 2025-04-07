package opekope2.optigui.mixin.screen;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.LoomScreenHandler;
import opekope2.optigui.util.Constants;
import opekope2.optigui.util.INbtConvertible;
import opekope2.optigui.util.InventoryUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LoomScreenHandler.class)
public abstract class LoomScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    private Inventory input;

    @Shadow
    @Final
    private Inventory output;

    @Shadow
    private List<RegistryEntry<BannerPattern>> bannerPatterns;

    @Shadow
    public abstract int getSelectedPattern();

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        compound.put(Constants.INVENTORY_KEY, InventoryUtil.createNbt(input, lookup));
        compound.put(Constants.RESULT_INVENTORY_KEY, InventoryUtil.createNbt(output, lookup));
        var patterns = new NbtList();
        for (var pattern : bannerPatterns) patterns.add(NbtString.of(pattern.getIdAsString()));
        compound.put("banner_patterns", patterns);
        var selected = getSelectedPattern();
        if (selected >= 0) compound.putString("selected_banner_pattern", bannerPatterns.get(selected).getIdAsString());
    }
}
