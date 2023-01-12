package opekope2.optigui.internal.properties

import net.minecraft.util.Identifier

internal class ShulkerBoxProperties(
    container: String,
    texture: Identifier,
    name: String?,
    biome: Identifier?,
    height: Int,
    val color: String?
) : GeneralProperties(container, texture, name, biome, height) {
    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + color.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean =
        if (other is ShulkerBoxProperties)
            super.equals(other)
                    && color == other.color
        else false
}
