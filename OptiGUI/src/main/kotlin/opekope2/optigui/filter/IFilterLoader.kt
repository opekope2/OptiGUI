package opekope2.optigui.filter

import net.minecraft.resource.ResourceReloader
import net.minecraft.util.Identifier
import opekope2.optigui.registry.RegistryBase
import java.util.function.Supplier

/**
 * A filter supplier that loads [filters][TextureReplacerFilter] from resources.
 * [IFilterLoader.getValue] should return the filters loaded in [IFilterLoader.reload].
 */
interface IFilterLoader : ResourceReloader, Supplier<List<TextureReplacerFilter>> {
    /**
     * Filter supplier registry.
     */
    companion object Registry : RegistryBase<Identifier, IFilterLoader>()
}
