package opekope2.optigui.properties

/**
 * Properties for chests.
 *
 * @see net.minecraft.block.entity.ChestBlockEntity
 * @see opekope2.optigui.properties.impl.ChestProperties
 */
interface IChestProperties : IRedstoneComparatorProperties {
    /**
     * Whether the chest is a large chest.
     */
    val isLarge: Boolean
}
