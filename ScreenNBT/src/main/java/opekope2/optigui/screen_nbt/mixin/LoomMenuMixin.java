package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.level.block.entity.BannerPattern;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LoomMenu.class)
public abstract class LoomMenuMixin implements INbtConvertible {
    @Shadow
    @Final
    private Container inputContainer;

    @Shadow
    @Final
    private Container outputContainer;

    @Shadow
    private List<Holder<BannerPattern>> selectablePatterns;

    @Shadow
    public abstract int getSelectedBannerPatternIndex();

    @Shadow
    protected abstract boolean isValidPatternIndex(int index);

    @Override
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);

        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(inputContainer, registryAccess));
        compound.put(RESULT_INVENTORY_KEY, NbtUtil.createInventoryNbt(outputContainer, registryAccess));

        var patterns = new ListTag();
        for (var pattern : selectablePatterns) patterns.add(StringTag.valueOf(pattern.getRegisteredName()));
        compound.put("banner_patterns", patterns);

        var i = getSelectedBannerPatternIndex();
        if (isValidPatternIndex(i)) compound.put("selected", patterns.get(i));

        return compound;
    }
}
