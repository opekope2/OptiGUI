package opekope2.optigui.internal.fabric

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.nbt.NbtCompound
import opekope2.optigui.resource.format.json.ILoadTimeNbtSupplier

internal object FabricModsNbtSupplier : ILoadTimeNbtSupplier {
    override fun get() = NbtCompound().apply {
        for (modContainer in FabricLoader.getInstance().allMods) {
            val modMeta = modContainer.metadata
            put(modMeta.id, NbtCompound().apply {
                putString("version", modMeta.version.friendlyString)
            })
        }
    }
}
