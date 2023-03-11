package opekope2.optifinecompat.properties

import net.minecraft.util.Identifier

/**
 * Shulker box OptiFine container properties.
 *
 * @param color The color of the shulker box
 */
data class ShulkerBoxProperties(
    override val container: String,
    override val texture: Identifier,
    override val name: String?,
    override val biome: Identifier?,
    override val height: Int,
    val color: String?
) : GeneralProperties(container, texture, name, biome, height)
