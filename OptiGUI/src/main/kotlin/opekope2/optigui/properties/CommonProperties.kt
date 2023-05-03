package opekope2.optigui.properties

import net.minecraft.util.Identifier

/**
 * Common properties for built-in containers.
 *
 * @see DefaultProperties
 */
interface CommonProperties : IndependentProperties {
    /**
     * The identifier of the container. Same as in the `/summon` or `/setblock` commands
     */
    val container: Identifier

    /**
     * Custom entity or block entity name (renamed in an anvil or with a name tag)
     */
    val name: String?

    /**
     * Biome of a block entity or entity
     */
    val biome: Identifier?

    /**
     * Y-coordinate of a block entity or entity
     */
    val height: Int
}
