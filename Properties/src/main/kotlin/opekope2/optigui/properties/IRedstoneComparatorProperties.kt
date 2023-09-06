package opekope2.optigui.properties

import opekope2.optigui.api.interaction.IInteractionData
import java.util.function.BiConsumer

/**
 * Properties for objects, which can be read from using a redstone comparator.
 *
 * @see opekope2.optigui.properties.impl.RedstoneComparatorProperties
 * @see opekope2.optigui.properties.impl.CommonRedstoneComparatorProperties
 */
interface IRedstoneComparatorProperties : IInteractionData {
    /**
     * The output of the redstone comparator reading the container.
     */
    val comparatorOutput: Int

    override fun writeSelectors(appendSelector: BiConsumer<String, String>) {
        appendSelector.accept("comparator.output", comparatorOutput.toString())
    }
}
