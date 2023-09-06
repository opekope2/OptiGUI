package opekope2.optigui.properties

import opekope2.optigui.api.interaction.IInteractionData
import java.util.function.BiConsumer

/**
 * Properties for horses.
 *
 * @see net.minecraft.entity.passive.HorseEntity
 * @see opekope2.optigui.properties.impl.HorseProperties
 */
interface IHorseProperties : IHorseLikeProperties, IInteractionData {
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

    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        super.writeSelectors(appendSelector)
        appendSelector.accept("horse.variants", variant)
        appendSelector.accept("horse.markings", marking)
    }
}
