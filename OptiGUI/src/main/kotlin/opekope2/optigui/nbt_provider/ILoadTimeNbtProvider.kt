package opekope2.optigui.nbt_provider

import net.minecraft.nbt.NbtElement
import opekope2.optigui.registry.RegistryBase
import java.util.function.Supplier

/**
 * Load-time NBT provider to decide which filters to load.
 */
fun interface ILoadTimeNbtProvider : Supplier<NbtElement> {
    /**
     * Load-time NBT provider registry.
     */
    companion object Registry : RegistryBase<String, ILoadTimeNbtProvider>()
}
