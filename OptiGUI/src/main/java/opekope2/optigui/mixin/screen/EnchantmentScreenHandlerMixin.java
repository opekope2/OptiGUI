package opekope2.optigui.mixin.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.EnchantmentScreenHandler;
import opekope2.optigui.util.Constants;
import opekope2.optigui.util.INbtConvertible;
import opekope2.optigui.util.InventoryUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin implements INbtConvertible {
    @Shadow
    @Final
    private Inventory inventory;

    @Shadow
    @Final
    public int[] enchantmentId;

    @Shadow
    @Final
    public int[] enchantmentLevel;

    @Shadow
    @Final
    public int[] enchantmentPower;

    @Override
    public void optiGui_writeNbt(@NotNull NbtCompound compound, @NotNull RegistryWrapper.WrapperLookup lookup) {
        compound.put(Constants.INVENTORY_KEY, InventoryUtil.createNbt(inventory, lookup));
        var world = MinecraftClient.getInstance().world;
        if (world == null) return;
        var enchantmentRegistry = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
        var enchantments = new NbtList();
        for (int i = 0; i < 3; i++) {
            var enchantmentReference = enchantmentRegistry.getEntry(enchantmentId[i]);
            if (enchantmentReference.isEmpty()) continue;

            var enchantment = new NbtCompound();
            enchantment.putString("id", enchantmentReference.get().getIdAsString());
            enchantment.putInt("level", enchantmentLevel[i]);
            enchantment.putInt("required_xp_level", enchantmentPower[i]);
            enchantments.add(enchantment);
        }
        compound.put("enchantments", enchantments);
    }
}
