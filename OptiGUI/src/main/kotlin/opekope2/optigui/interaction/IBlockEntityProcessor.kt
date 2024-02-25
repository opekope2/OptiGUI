package opekope2.optigui.interaction

import net.minecraft.block.entity.BlockEntity
import opekope2.optigui.filter.IFilter
import opekope2.optigui.registry.BlockEntityProcessorRegistry

/**
 * A block entity processor, which extracts information from a block entity for further processing by filters.
 * This class provides [Interaction.data] (converts a block entity to an object, where properties of the block entity
 * are stored, which will be processed by filters supporting it).
 *
 * Each block entity can have one processor registered with [BlockEntityProcessorRegistry.set].
 *
 * If a GUI screen is open, the processor of the interacted block entity runs each tick, so [process] should execute
 * quickly.
 *
 * The result class of [process] should override the [Object.equals] method, because filters will only be evaluated,
 * if the processor returns a different object, because the block entity was changed (for example, moved to a different
 * biome).
 *
 * @param T The block entity type the processor accepts
 */
fun interface IBlockEntityProcessor<T : BlockEntity> {
    /**
     * Processes a block entity.
     *
     * @param blockEntity The source block entity
     * @return An object, which will be included in [Interaction.data], and processed by [IFilter.evaluate]
     */
    fun process(blockEntity: T): Interaction.IExportableData?
}
