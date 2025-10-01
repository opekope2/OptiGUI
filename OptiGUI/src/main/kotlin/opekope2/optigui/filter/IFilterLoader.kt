package opekope2.optigui.filter

import com.google.common.collect.Multimap
import net.minecraft.resource.ResourceReloader
import net.minecraft.util.Identifier
import opekope2.optigui.filter.texture_changer.TextureChangerFilter
import opekope2.optigui.interaction.InteractionTarget
import opekope2.optigui.registry.RegistryBase

/**
 * A filter supplier that loads [filters][TextureChangerFilter] from resources.
 */
interface IFilterLoader : ResourceReloader {
    /**
     * Gets the filters loaded in [IFilterLoader.reload].
     */
    val filters: Multimap<InteractionTarget, TextureChangerFilter>

    /**
     * Filter supplier registry.
     */
    companion object Registry : RegistryBase<Identifier, IFilterLoader>()
}
