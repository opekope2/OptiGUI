package opekope2.optigui.properties.impl

import net.minecraft.util.Identifier
import opekope2.optigui.properties.IGeneralProperties

/**
 * Default implementation of [IGeneralProperties].
 */
data class GeneralProperties(
    override val name: String?,
    override val biome: Identifier,
    override val height: Int,
) : IGeneralProperties
