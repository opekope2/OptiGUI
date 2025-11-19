package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.EnchantmentMenu;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin implements INbtConvertible {
    @Shadow
    @Final
    private Container enchantSlots;

    @Shadow
    @Final
    public int[] enchantClue;

    @Shadow
    @Final
    public int[] levelClue;

    @Shadow
    @Final
    public int[] costs;

    @Override
    public void optiGui_writeNbt(CompoundTag compound, HolderLookup.Provider lookup) {
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(enchantSlots, lookup));
        var world = Minecraft.getInstance().level;
        if (world == null) return;
        var enchantmentRegistry = world.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        var enchantments = new ListTag();
        for (int i = 0; i < 3; i++) {
            var enchantmentReference = enchantmentRegistry.getHolder(enchantClue[i]);
            if (enchantmentReference.isEmpty()) continue;

            var enchantment = new CompoundTag();
            enchantment.putString("id", enchantmentReference.get().getRegisteredName());
            enchantment.putInt("level", levelClue[i]);
            enchantment.putInt("cost", costs[i]);
            enchantments.add(enchantment);
        }
        compound.put("enchantments", enchantments);
    }
}
