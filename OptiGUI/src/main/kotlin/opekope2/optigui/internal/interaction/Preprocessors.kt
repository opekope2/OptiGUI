package opekope2.optigui.internal.interaction

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity

internal val blockEntityPreprocessors =
    mutableMapOf<Class<out BlockEntity>, IdentifiableBlockEntityPreprocessor>()
internal val entityPreprocessors =
    mutableMapOf<Class<out Entity>, IdentifiableEntityPreprocessor>()
