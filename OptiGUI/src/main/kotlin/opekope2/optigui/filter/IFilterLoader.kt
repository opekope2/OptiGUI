package opekope2.optigui.filter

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.util.Identifier
import opekope2.optigui.registry.RegistryBase
import java.util.function.Supplier

/**
 * A filter supplier that loads [filters][TextureReplacerFilter] from resources.
 * [IFilterLoader.getValue] should return the filters loaded in [IFilterLoader.reload].
 */
interface IFilterLoader : IdentifiableResourceReloadListener, Supplier<List<TextureReplacerFilter>> {
    /**
     * Filter supplier registry.
     *
     * When registering a filter supplier, the registered ID must match [IFilterLoader.getFabricId]
     */
    companion object Registry : RegistryBase<Identifier, IFilterLoader>() {
        @Deprecated("Use register(IFilterLoader)", replaceWith = ReplaceWith("IFilterLoader.register(value)"))
        override fun register(key: Identifier, value: IFilterLoader) {
            require(key == value.fabricId) { "Key `$key` does not match fabricId `$value`" }
            register(value)
        }

        /**
         * Registers an entry to this registry.
         * The key is obtained from [IFilterLoader.getFabricId].
         *
         * @param value The value to register
         */
        fun register(value: IFilterLoader) {
            super.register(value.fabricId, value)
        }
    }
}
