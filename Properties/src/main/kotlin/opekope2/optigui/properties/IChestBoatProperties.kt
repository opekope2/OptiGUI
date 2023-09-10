package opekope2.optigui.properties

import opekope2.optigui.api.interaction.IInteractionData
import java.util.function.BiConsumer

/**
 * Properties for chest boats.
 *
 * @see net.minecraft.entity.vehicle.ChestBoatEntity
 * @see opekope2.optigui.properties.impl.ChestBoatProperties
 */
interface IChestBoatProperties : IInteractionData {
    /**
     * Gets the variant (wood material) of the boat.
     *
     * @see net.minecraft.entity.vehicle.ChestBoatEntity.getVariant
     * @see net.minecraft.entity.vehicle.BoatEntity.Type
     */
    val variant: String

    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        appendSelector.accept("chest_boat.variants", variant)
    }
}
