package opekope2.optigui.internal.properties

import net.minecraft.util.Identifier

internal class ChestProperties(
    container: String,
    texture: Identifier,
    name: String?,
    biome: Identifier?,
    height: Int,
    val large: Boolean? = null,
    val trapped: Boolean? = null,
    val christmas: Boolean? = null,
    val ender: Boolean? = null
) : GeneralProperties(container, texture, name, biome, height) {
    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (large?.hashCode() ?: 0)
        result = 31 * result + (trapped?.hashCode() ?: 0)
        result = 31 * result + (christmas?.hashCode() ?: 0)
        result = 31 * result + (ender?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean =
        if (other is ChestProperties)
            super.equals(other)
                    && large == other.large
                    && trapped == other.trapped
                    && christmas == other.christmas
                    && ender == other.ender
        else false
}
