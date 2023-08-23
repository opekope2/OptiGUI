package opekope2.optigui.properties.impl

import opekope2.optigui.properties.ICommonProperties
import opekope2.optigui.properties.IDonkeyProperties
import opekope2.optigui.properties.ILlamaProperties

/**
 * Implementation of [ILlamaProperties] for llamas.
 */
class LlamaProperties(
    val commonProperties: ICommonProperties,
    val donkeyProperties: IDonkeyProperties,
    override val carpetColor: String?,
) : ICommonProperties by commonProperties, IDonkeyProperties by donkeyProperties, ILlamaProperties {
    constructor(donkeyProperties: DonkeyProperties, carpetColor: String?)
            : this(donkeyProperties, donkeyProperties, carpetColor)
}
