package opekope2.optigui.interaction

import net.minecraft.block.entity.BlockEntity

/**
 * A block entity preprocessor, which extracts information from a block entity for further processing by filters.
 * This class provides [Interaction.data] (converts a block entity to an object,
 * where properties of the block entity are stored, which will be processed by filters supporting it).
 *
 * Each block entity can have one preprocessor registered with [registerPreprocessor].
 *
 * If a GUI screen is open, the preprocessor of the interacted block entity runs each tick,
 * so [process] should execute quickly.
 *
 * The result class of [process] should override the [Object.equals] method,
 * because filters will only be evaluated, if the preprocessor returns a different object,
 * because the block entity was changed (for example, moved to a different biome).
 */
fun interface BlockEntityPreprocessor {
    /**
     * Processes a block entity.
     *
     * @param blockEntity The source block entity
     * @return An object, which will be included in [Interaction.data], and processed by [opekope2.filter.Filter.evaluate]
     */
    fun process(blockEntity: BlockEntity): Any?
}
