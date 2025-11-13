package opekope2.optigui.internal.fabric.nbt_provider

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.nbt.CompoundTag
import opekope2.optigui.nbt_provider.ILoadTimeNbtProvider

internal object FabricModsNbtProvider : ILoadTimeNbtProvider {
    override fun get() = CompoundTag().apply {
        for (modContainer in FabricLoader.getInstance().allMods) {
            val modMeta = modContainer.metadata
            put(modMeta.id, CompoundTag().apply {
                putString("version", modMeta.version.friendlyString)
            })
        }
    }
}
