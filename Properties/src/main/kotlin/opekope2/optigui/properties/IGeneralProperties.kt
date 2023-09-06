package opekope2.optigui.properties

import net.minecraft.util.Identifier
import opekope2.optigui.api.interaction.IInteractionData
import java.util.function.BiConsumer

/**
 * Common properties for built-in containers.
 *
 * @see opekope2.optigui.properties.impl.GeneralProperties
 */
interface IGeneralProperties : IInteractionData {
    /**
     * Custom container name (renamed with an anvil or with a name tag).
     *
     * @see net.minecraft.util.Nameable.getCustomName
     * @see net.minecraft.entity.Entity.getCustomName
     */
    val name: String?

    /**
     * Biome the container is in.
     */
    val biome: Identifier

    /**
     * Y-coordinate of the container.
     */
    val height: Int

    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        name?.let { name -> appendSelector.accept("name", name) }
        appendSelector.accept("biomes", biome.toString())
        appendSelector.accept("heights", height.toString())
    }
}
