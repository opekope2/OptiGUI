package opekope2.optigui.interaction

import net.minecraft.entity.Entity

/**
 * An entity preprocessor, which extracts information from an entity for further processing by filters.
 * This class provides [Interaction.data] (converts an entity to an object,
 * where properties of the entity are stored, which will be processed by filters supporting it).
 *
 * Each entity can have one preprocessor registered with [registerPreprocessor].
 *
 * If a GUI screen is open, the preprocessor of the interacted entity runs each tick,
 * so [process] should execute quickly.
 *
 * The result class of [process] should override the [Object.equals] method,
 * because filters will only be evaluated, if the preprocessor returns a different object,
 * because the entity was changed (for example, moved to a different biome).
 */
fun interface EntityPreprocessor {
    /**
     * Creates an interaction data based on a block entity.
     *
     * @param entity The source entity
     * @return An object, which will be included in [Interaction.data], and processed by [opekope2.filter.Filter.evaluate]
     */
    fun process(entity: Entity): Any?
}
