package opekope2.optigui.properties.impl

import opekope2.optigui.properties.ICommonProperties
import opekope2.optigui.properties.IHorseLikeProperties
import opekope2.optigui.properties.IHorseProperties
import java.util.function.BiConsumer

/**
 * Implementation of [IHorseProperties] for horses.
 */
data class HorseProperties(
    private val commonProperties: ICommonProperties,
    private val horseLikeProperties: IHorseLikeProperties,
    override val variant: String,
    override val marking: String,
) : ICommonProperties by commonProperties, IHorseLikeProperties by horseLikeProperties, IHorseProperties {
    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        super<ICommonProperties>.writeSelectors(appendSelector)
        super<IHorseProperties>.writeSelectors(appendSelector)
    }
}
