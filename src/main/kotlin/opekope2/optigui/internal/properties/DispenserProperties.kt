package opekope2.optigui.internal.properties

import net.minecraft.util.Identifier

internal class DispenserProperties(
    container: String,
    texture: Identifier,
    name: String?,
    biome: Identifier?,
    height: Int,
    val variant: String
) : GeneralProperties(container, texture, name, biome, height) {
    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + variant.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        return if (other is DispenserProperties)
            super.equals(other)
                    && variant == other.variant
        else false
    }
}
