package opekope2.optigui.properties

/**
 * Properties for horses.
 *
 * @see net.minecraft.entity.passive.HorseEntity
 * @see opekope2.optigui.properties.impl.HorseProperties
 */
interface IHorseProperties : IHorseLikeProperties {
    /**
     * The variant of the horse.
     *
     * @see net.minecraft.entity.passive.HorseColor
     */
    val variant: String

    /**
     * The marking on the horse.
     *
     * @see net.minecraft.entity.passive.HorseMarking
     */
    val marking: String
}
