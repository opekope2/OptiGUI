package opekope2.optigui.properties.impl

import opekope2.optigui.properties.IChestBoatProperties
import opekope2.optigui.properties.ICommonProperties

/**
 * Implementation of [IChestBoatProperties] for chest boats.
 */
data class ChestBoatProperties(
    val commonProperties: ICommonProperties,
    override val variant: String,
) : ICommonProperties by commonProperties, IChestBoatProperties
