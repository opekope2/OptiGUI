package opekope2.optigui.internal.fabric

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.nbt.NbtCompound
import opekope2.optigui.nbt_provider.ILoadTimeNbtProvider

internal object FabricModsNbtProvider : ILoadTimeNbtProvider {
    override fun get() = NbtCompound().apply {
        for (modContainer in FabricLoader.getInstance().allMods) {
            val modMeta = modContainer.metadata
            put(modMeta.id, NbtCompound().apply {
                putString("version", modMeta.version.friendlyString)
            })
        }
    }
}
