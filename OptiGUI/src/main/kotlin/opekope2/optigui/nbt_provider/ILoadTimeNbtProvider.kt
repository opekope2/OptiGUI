package opekope2.optigui.nbt_provider

import net.minecraft.nbt.Tag
import opekope2.optigui.util.registry.RegistryBase
import java.util.function.Supplier

/**
 * Load-time NBT provider to decide which filters to load.
 */
fun interface ILoadTimeNbtProvider : Supplier<Tag> {
    /**
     * Load-time NBT provider registry.
     */
    companion object Registry : RegistryBase<String, ILoadTimeNbtProvider>()
}
