package opekope2.optigui.properties.impl

import opekope2.optigui.properties.ICommonProperties
import opekope2.optigui.properties.IDonkeyProperties
import opekope2.optigui.properties.ILlamaProperties
import java.util.function.BiConsumer

/**
 * Implementation of [ILlamaProperties] for llamas.
 */
class LlamaProperties(
    val commonProperties: ICommonProperties,
    val donkeyProperties: IDonkeyProperties,
    override val carpetColor: String?,
    override val variant: String,
) : ICommonProperties by commonProperties, IDonkeyProperties by donkeyProperties, ILlamaProperties {
    constructor(donkeyProperties: DonkeyProperties, carpetColor: String?, variant: String)
            : this(donkeyProperties, donkeyProperties, carpetColor, variant)

    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        super<ICommonProperties>.writeSelectors(appendSelector)
        super<ILlamaProperties>.writeSelectors(appendSelector)
    }
}
