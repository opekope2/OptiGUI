package opekope2.optigui.screen_nbt.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.enchantment.Enchantment;
import opekope2.optigui.screen_api.util.INbtConvertible;
import opekope2.optigui.screen_api.util.NbtUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

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
    public CompoundTag optiGui_asNbt(RegistryAccess registryAccess) {
        var compound = INbtConvertible.super.optiGui_asNbt(registryAccess);
        compound.put(INVENTORY_KEY, NbtUtil.createInventoryNbt(enchantSlots, registryAccess));
        compound.put("enchantments", optiGui_getEnchantments(registryAccess.registryOrThrow(Registries.ENCHANTMENT)));
        return compound;
    }

    @Unique
    private ListTag optiGui_getEnchantments(Registry<Enchantment> enchantmentRegistry) {
        var enchantments = new ListTag();
        for (int i = 0; i < 3; i++) {
            var enchantmentReference = enchantmentRegistry.getHolder(enchantClue[i]);
            if (enchantmentReference.isEmpty()) continue;
            enchantments.add(optiGui_getEnchantment(enchantmentReference.get(), i));
        }
        return enchantments;
    }

    @Unique
    private CompoundTag optiGui_getEnchantment(Holder.Reference<Enchantment> enchantmentReference, int i) {
        var enchantment = new CompoundTag();
        enchantment.putString("id", enchantmentReference.getRegisteredName());
        enchantment.putInt("level", levelClue[i]);
        enchantment.putInt("cost", costs[i]);
        return enchantment;
    }
}
