package opekope2.optigui.internal.properties

import net.minecraft.util.Identifier

internal class ChestProperties(
    container: String,
    texture: Identifier,
    name: String?,
    biome: Identifier?,
    height: Int,
    val large: Boolean,
    val trapped: Boolean,
    val christmas: Boolean,
    val ender: Boolean,
    val barrel: Boolean
) : GeneralProperties(container, texture, name, biome, height) {
    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + large.hashCode()
        result = 31 * result + trapped.hashCode()
        result = 31 * result + christmas.hashCode()
        result = 31 * result + ender.hashCode()
        result = 31 * result + barrel.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean =
        if (other is ChestProperties)
            super.equals(other)
                    && large == other.large
                    && trapped == other.trapped
                    && christmas == other.christmas
                    && ender == other.ender
                    && barrel == other.barrel
        else false
}
