package opekope2.optigui.properties

import opekope2.optigui.api.interaction.IInteractionData
import java.util.function.BiConsumer

/**
 * Properties for donkey-like entities.
 *
 * @see net.minecraft.entity.passive.AbstractDonkeyEntity
 * @see opekope2.optigui.properties.impl.DonkeyProperties
 */
interface IDonkeyProperties : IHorseLikeProperties, IInteractionData {
    /**
     * Whether the donkey has a chest
     *
     * @see net.minecraft.entity.passive.AbstractDonkeyEntity.hasChest
     */
    val hasChest: Boolean

    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        super.writeSelectors(appendSelector)
        appendSelector.accept("donkey.has_chest", hasChest.toString())
    }
}
