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
    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
    }
}
