package opekope2.optigui.resource.load

import net.minecraft.nbt.NbtElement
import opekope2.optigui.registry.RegistryBase
import java.util.function.Supplier

/**
 * Load-time NBT supplier to decide which filters to load.
 */
fun interface ILoadTimeNbtSupplier : Supplier<NbtElement> {
    /**
     * Load-time NBT supplier registry.
     */
    companion object Registry : RegistryBase<String, ILoadTimeNbtSupplier>()
}
