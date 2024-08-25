package opekope2.optigui.filter

import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import java.util.function.Supplier

/**
 * Load-time NBT filter registration utility.
 */
object NbtFilters {
    private val loadTimeFilterComponents = mutableMapOf<Identifier, Supplier<NbtCompound>>()

    /**
     * Registers a component supplier for the given component ID to be evaluated at load-time.
     * This is used to determine whether a resource should be loaded.
     *
     * @param componentId The component ID resources can refer to. This does not have to be a registered data component
     * type, but ID collisions should be avoided by using your mod ID and a path not registered as a data component type
     * @param componentSupplier The supplier of the component
     */
    @JvmStatic
    fun evaluateAtLoadTime(componentId: Identifier, componentSupplier: Supplier<NbtCompound>) {
        if (componentId in loadTimeFilterComponents) throw IllegalArgumentException("Component `$componentId` already has a registered load-time component supplier")
        loadTimeFilterComponents[componentId] = componentSupplier
    }

    /**
     * Checks if the given component is evaluated at load time.
     *
     * @param componentId The component ID resources can refer to
     */
    @JvmStatic
    fun isEvaluatedAtLoadTime(componentId: Identifier) = componentId in loadTimeFilterComponents

    /**
     * Gets the load-time component supplier registered for the given component ID.
     *
     * @param componentId The component ID resources can refer to
     */
    @JvmStatic
    fun getLoadTimeComponentSupplier(componentId: Identifier) = loadTimeFilterComponents[componentId]
}
