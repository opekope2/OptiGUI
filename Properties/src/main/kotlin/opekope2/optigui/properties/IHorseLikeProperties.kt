package opekope2.optigui.properties

import opekope2.optigui.api.interaction.IInteractionData
import java.util.function.BiConsumer

/**
 * Properties for horse-like entities.
 *
 * @see net.minecraft.entity.passive.AbstractHorseEntity
 * @see opekope2.optigui.properties.impl.HorseLikeProperties
 */
interface IHorseLikeProperties : IInteractionData {
    /**
     * Whether the horse is saddled.
     *
     * @see net.minecraft.entity.passive.AbstractHorseEntity.isSaddled
     */
    val hasSaddle: Boolean

    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        appendSelector.accept("horse.has_saddle", hasSaddle.toString())
    }
}
