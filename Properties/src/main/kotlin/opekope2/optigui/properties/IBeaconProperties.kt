package opekope2.optigui.properties

import opekope2.optigui.api.interaction.IInteractionData
import java.util.function.BiConsumer

/**
 * Properties for beacons.
 *
 * @see net.minecraft.block.entity.BeaconBlockEntity
 * @see opekope2.optigui.properties.impl.BeaconProperties
 */
interface IBeaconProperties : IInteractionData {
    /**
     * Gets the level of the beacon.
     *
     * @see net.minecraft.block.entity.BeaconBlockEntity.level
     */
    val level: Int

    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        appendSelector.accept("beacon.levels", level.toString())
    }
}
