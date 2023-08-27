package opekope2.optigui.properties

/**
 * Properties for donkey-like entities.
 *
 * @see net.minecraft.entity.passive.AbstractDonkeyEntity
 * @see opekope2.optigui.properties.impl.DonkeyProperties
 */
interface IDonkeyProperties : IHorseLikeProperties {
    /**
     * Whether the donkey has a chest
     *
     * @see net.minecraft.entity.passive.AbstractDonkeyEntity.hasChest
     */
    val hasChest: Boolean
}
