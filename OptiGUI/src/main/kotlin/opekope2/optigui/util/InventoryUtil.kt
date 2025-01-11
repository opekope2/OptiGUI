@file: JvmName("InventoryUtil")

package opekope2.optigui.util

import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtOps
import net.minecraft.registry.RegistryWrapper

/**
 * Encodes an inventory to an [NbtList].
 *
 * @param lookup The registry lookup used to encode NBT
 */
fun Inventory.createNbt(lookup: RegistryWrapper.WrapperLookup) = (0 until size()).mapTo(NbtList()) {
    ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, getStack(it)).getOrThrow(false) { }
}
