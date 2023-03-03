package opekope2.optifinecompat.properties

import net.minecraft.util.Identifier
import opekope2.util.isChristmas

/**
 * Chest OptiFine container properties.
 *
 * @param large The level of a beacon
 * @param trapped Whether a chest is trapped
 * @param christmas Whether now is Christmas
 * @param ender Whether a chest is an ender chest
 * @param barrel Whether a container is a barrel
 *
 * @see isChristmas
 */
data class ChestProperties(
    override val container: String,
    override val texture: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val large: Boolean,
    val trapped: Boolean,
    val christmas: Boolean,
    val ender: Boolean,
    val barrel: Boolean
) : GeneralProperties(container, texture, name, biome, height)
