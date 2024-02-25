package opekope2.optigui.internal.interaction

import net.minecraft.block.entity.BlockEntity

internal object PreprocessorStore {
    val blockEntityPreprocessors =
        mutableMapOf<Class<out BlockEntity>, IdentifiableBlockEntityPreprocessor<BlockEntity>>()

    fun add(type: Class<out BlockEntity>, processor: IdentifiableBlockEntityPreprocessor<BlockEntity>): Boolean {
        if (type in blockEntityPreprocessors) return false

        blockEntityPreprocessors[type] = processor
        return true
    }
}
