package opekope2.optigui.properties.impl

import net.minecraft.util.Identifier
import opekope2.optigui.properties.ICommonProperties
import opekope2.optigui.properties.IVillagerProperties

/**
 * Implementation of [IVillagerProperties] for villagers.
 */
data class VillagerProperties(
    val commonProperties: ICommonProperties,
    override val profession: Identifier,
    override val level: Int,
    override val type: Identifier
) : ICommonProperties by commonProperties, IVillagerProperties
