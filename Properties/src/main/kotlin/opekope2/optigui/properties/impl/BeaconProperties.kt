package opekope2.optigui.properties.impl

import opekope2.optigui.properties.IBeaconProperties
import opekope2.optigui.properties.ICommonProperties

/**
 * Implementation of [IBeaconProperties] for beacons.
 */
data class BeaconProperties(
    val commonProperties: ICommonProperties,
    override val level: Int,
) : ICommonProperties by commonProperties, IBeaconProperties
