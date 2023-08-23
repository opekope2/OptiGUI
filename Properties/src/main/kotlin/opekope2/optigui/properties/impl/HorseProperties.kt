package opekope2.optigui.properties.impl

import opekope2.optigui.properties.ICommonProperties
import opekope2.optigui.properties.IHorseLikeProperties
import opekope2.optigui.properties.IHorseProperties

/**
 * Implementation of [IHorseProperties] for horses.
 */
data class HorseProperties(
    val commonProperties: ICommonProperties,
    val horseLikeProperties: IHorseLikeProperties,
    override val variant: String,
    override val marking: String,
) : ICommonProperties by commonProperties, IHorseLikeProperties by horseLikeProperties, IHorseProperties
