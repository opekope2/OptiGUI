package opekope2.optigui.properties

import net.minecraft.util.Identifier

/**
 * Properties for chests and trapped chests.
 *
 * @param isLarge Whether a chest is large
 * @param isChristmas Whether it's Christmastime
 */
data class ChestProperties(
    override val container: String,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val isLarge: Boolean,
    val isChristmas: Boolean
) : CommonProperties
