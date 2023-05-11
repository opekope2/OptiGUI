package opekope2.optigui.internal.interaction

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity

internal object PreprocessorStore {
    val blockEntityPreprocessors =
        mutableMapOf<Class<out BlockEntity>, IdentifiableBlockEntityPreprocessor<BlockEntity>>()
    val entityPreprocessors =
        mutableMapOf<Class<out Entity>, IdentifiableEntityPreprocessor<Entity>>()

    fun add(type: Class<out BlockEntity>, processor: IdentifiableBlockEntityPreprocessor<BlockEntity>): Boolean {
        if (type in blockEntityPreprocessors) return false

        blockEntityPreprocessors[type] = processor
        return true
    }

    fun add(type: Class<out Entity>, processor: IdentifiableEntityPreprocessor<Entity>): Boolean {
        if (type in entityPreprocessors) return false

        entityPreprocessors[type] = processor
        return true
    }
}
