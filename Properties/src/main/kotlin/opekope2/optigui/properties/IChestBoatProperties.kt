package opekope2.optigui.properties

/**
 * Properties for chest boats.
 *
 * @see net.minecraft.entity.vehicle.ChestBoatEntity
 * @see opekope2.optigui.properties.impl.ChestBoatProperties
 */
interface IChestBoatProperties {
    /**
     * Gets the variant (wood material) of the boat.
     *
     * @see net.minecraft.entity.vehicle.ChestBoatEntity.getVariant
     * @see net.minecraft.entity.vehicle.BoatEntity.Type
     */
    val variant: String
}
