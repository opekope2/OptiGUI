package opekope2.optigui.internal.properties

import net.minecraft.util.Identifier

internal open class GeneralProperties(
    val container: String,
    val texture: Identifier,
    val name: String?,
    val biome: Identifier?,
    val height: Int
) {
    override fun hashCode(): Int {
        var result = container.hashCode()
        result = 31 * result + texture.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + biome.hashCode()
        result = 31 * result + height
        return result
    }

    override fun equals(other: Any?): Boolean =
        if (other is GeneralProperties)
            hashCode() == other.hashCode()
                    && container == other.container
                    && texture == other.texture
                    && name == other.name
                    && biome == other.biome
                    && height == other.height
        else false
}
