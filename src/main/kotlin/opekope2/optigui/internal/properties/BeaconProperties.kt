package opekope2.optigui.internal.properties

import net.minecraft.util.Identifier

internal class BeaconProperties(
    container: String,
    texture: Identifier,
    name: String?,
    biome: Identifier?,
    height: Int,
    val level: Int
) : GeneralProperties(container, texture, name, biome, height) {
    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + level
        return result
    }

    override fun equals(other: Any?): Boolean =
        if (other is BeaconProperties)
            super.equals(other)
                    && level == other.level
        else false
}
