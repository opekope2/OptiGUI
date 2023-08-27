package opekope2.optigui.properties

/**
 * Properties for objects, which can be read from using a redstone comparator.
 *
 * @see opekope2.optigui.properties.impl.RedstoneComparatorProperties
 * @see opekope2.optigui.properties.impl.CommonRedstoneComparatorProperties
 */
interface IRedstoneComparatorProperties {
    /**
     * The output of the redstone comparator reading the container.
     */
    val comparatorOutput: Int
}
