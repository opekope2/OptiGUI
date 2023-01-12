package opekope2.optigui.internal.properties

import net.minecraft.util.Identifier

internal class VillagerProperties(
    container: String,
    texture: Identifier,
    name: String?,
    biome: Identifier?,
    height: Int,
    val profession: Identifier?,
    val level: Int?
) : GeneralProperties(container, texture, name, biome, height) {
    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (profession?.hashCode() ?: 0)
        result = 31 * result + (level?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean =
        if (other is VillagerProperties)
            super.equals(other)
                    && profession == other.profession
                    && level == other.level
        else false
}
