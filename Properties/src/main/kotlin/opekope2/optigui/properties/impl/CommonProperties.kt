package opekope2.optigui.properties.impl

import opekope2.optigui.properties.ICommonProperties
import opekope2.optigui.properties.IGeneralProperties
import opekope2.optigui.properties.IIndependentProperties
import java.util.function.BiConsumer

/**
 * Default implementation of [ICommonProperties].
 */
data class CommonProperties(
    val generalProperties: IGeneralProperties,
    val independentProperties: IIndependentProperties,
) : ICommonProperties, IGeneralProperties by generalProperties, IIndependentProperties by independentProperties {
    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        super<ICommonProperties>.writeSelectors(appendSelector)
    }
}
