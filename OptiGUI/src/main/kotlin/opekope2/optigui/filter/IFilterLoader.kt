package opekope2.optigui.filter

import net.minecraft.resource.ResourceReloader
import net.minecraft.util.Identifier
import opekope2.optigui.registry.RegistryBase
import java.util.function.Supplier

/**
 * A filter supplier that loads [filters][TextureChangerFilter] from resources.
 * [IFilterLoader.get] should return the filters loaded in [IFilterLoader.reload].
 */
interface IFilterLoader : ResourceReloader, Supplier<List<TextureChangerFilter>> {
    /**
     * Gets the filters loaded in [IFilterLoader.reload].
     */
    override fun get(): List<TextureChangerFilter>

    /**
     * Filter supplier registry.
     */
    companion object Registry : RegistryBase<Identifier, IFilterLoader>()
}
