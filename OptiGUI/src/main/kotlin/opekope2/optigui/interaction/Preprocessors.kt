@file: JvmName("Preprocessors")

package opekope2.optigui.interaction

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity

internal val blockEntityPreprocessors = mutableMapOf<Class<out BlockEntity>, BlockEntityPreprocessor>()
internal val entityPreprocessors = mutableMapOf<Class<out Entity>, EntityPreprocessor>()

/**
 * Registers the preprocessor for a block entity.
 *
 * @param type The block entity type the preprocessor processes
 * @param processor The block entity preprocessor instance
 * @return `true` if registration is successful, `false` if the given block entity already has a preprocessor registered
 */
fun registerPreprocessor(type: Class<out BlockEntity>, processor: BlockEntityPreprocessor): Boolean {
    if (type in blockEntityPreprocessors) return false

    blockEntityPreprocessors[type] = processor
    return true
}

/**
 * Registers the preprocessor for a block entity.
 *
 * @param T The block entity type the preprocessor processes
 * @param processor The block entity preprocessor instance
 * @return `true` if registration is successful, `false` if the given block entity already has a preprocessor registered
 */
inline fun <reified T : BlockEntity> registerPreprocessor(processor: BlockEntityPreprocessor) =
    registerPreprocessor(T::class.java, processor)

/**
 * Registers the preprocessor for an entity.
 *
 * @param type The entity type the preprocessor processes
 * @param processor The entity preprocessor instance
 * @return `true` if registration is successful, `false` if the given entity already has a preprocessor registered
 */
fun registerPreprocessor(type: Class<out Entity>, processor: EntityPreprocessor): Boolean {
    if (type in entityPreprocessors) return false

    entityPreprocessors[type] = processor
    return true
}

/**
 * Registers the preprocessor for an entity.
 *
 * @param T The entity type the preprocessor processes
 * @param processor The entity preprocessor instance
 * @return `true` if registration is successful, `false` if the given entity already has a preprocessor registered
 */
inline fun <reified T : Entity> registerPreprocessor(processor: EntityPreprocessor) =
    registerPreprocessor(T::class.java, processor)
