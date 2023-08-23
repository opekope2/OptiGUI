package opekope2.optigui.properties

/**
 * Properties for beacons.
 *
 * @see net.minecraft.block.entity.BeaconBlockEntity
 * @see opekope2.optigui.properties.impl.BeaconProperties
 */
interface IBeaconProperties {
    /**
     * Gets the level of the beacon.
     *
     * @see net.minecraft.block.entity.BeaconBlockEntity.level
     */
    val level: Int
}
