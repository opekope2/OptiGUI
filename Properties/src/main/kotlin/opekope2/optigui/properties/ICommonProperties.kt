package opekope2.optigui.properties

import java.util.function.BiConsumer

/**
 * Common properties for vanilla containers consisting of general properties and independent properties.
 *
 * @see opekope2.optigui.properties.impl.CommonProperties
 */
interface ICommonProperties : IGeneralProperties, IIndependentProperties {
    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        super<IGeneralProperties>.writeSelectors(appendSelector)
        super<IIndependentProperties>.writeSelectors(appendSelector)
    }
}
