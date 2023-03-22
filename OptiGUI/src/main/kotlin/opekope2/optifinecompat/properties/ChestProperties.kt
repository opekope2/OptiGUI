package opekope2.optifinecompat.properties

import net.minecraft.util.Identifier

/**
 * Chest OptiFine container properties.
 *
 * @param isLarge Whether a chest is large
 * @param isTrapped Whether a chest is trapped
 * @param isChristmas Whether now is Christmas
 * @param isEnder Whether a chest is an ender chest
 * @param isBarrel Whether a container is a barrel
 * @param isMinecart Whether a container is a chest minecart
 *
 * @see isChristmas
 */
data class ChestProperties(
    override val container: String,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val isLarge: Boolean,
    val isTrapped: Boolean,
    val isChristmas: Boolean,
    val isEnder: Boolean,
    val isBarrel: Boolean,
    val isMinecart: Boolean
) : GeneralProperties(container, name, biome, height)
