package opekope2.optigui.resource

import net.minecraft.util.Identifier

/**
 * Represents unprocessed filter data.
 */
interface IRawFilterData {
    /**
     * The priority of the created filter in the evaluation chain. Higher priority means earlier evaluation.
     */
    val priority: Int
        get() = 0

    /**
     * The originating resource.
     */
    val resource: Identifier

    /**
     * The identifier of the container (if applicable). If this is `null`, the resulting filter will be evaluated after
     * the matching container's filters.
     */
    val container: Identifier?

    /**
     * The set of textures the filter can replace.
     */
    val replaceableTextures: Set<Identifier>

    /**
     * The replacement texture of the filter.
     */
    val replacementTexture: Identifier

    /**
     * The raw selector data as key-value pairs.
     */
    val rawSelectorData: Iterable<Pair<String, String>>
}
