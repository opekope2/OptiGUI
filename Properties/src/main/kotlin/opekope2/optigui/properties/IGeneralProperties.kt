package opekope2.optigui.properties

import net.minecraft.util.Identifier

/**
 * Common properties for built-in containers.
 *
 * @see opekope2.optigui.properties.impl.GeneralProperties
 */
interface IGeneralProperties {
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
}
