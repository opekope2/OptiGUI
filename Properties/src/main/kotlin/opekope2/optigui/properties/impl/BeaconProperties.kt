package opekope2.optigui.properties.impl

import opekope2.optigui.properties.IBeaconProperties
import opekope2.optigui.properties.ICommonProperties
import java.util.function.BiConsumer

/**
 * Implementation of [IBeaconProperties] for beacons.
 */
data class BeaconProperties(
    private val commonProperties: ICommonProperties,
    override val level: Int,
) : ICommonProperties by commonProperties, IBeaconProperties {
    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        super<ICommonProperties>.writeSelectors(appendSelector)
        super<IBeaconProperties>.writeSelectors(appendSelector)
    }
}
