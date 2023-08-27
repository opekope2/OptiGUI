package opekope2.optigui.impl

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import opekope2.optigui.api.IOptiGuiApi
import opekope2.optigui.api.interaction.IBlockEntityProcessor
import opekope2.optigui.api.interaction.IEntityProcessor
import opekope2.optigui.api.interaction.IInteractor
import opekope2.optigui.internal.TextureReplacer
import opekope2.optigui.internal.interaction.blockEntityProcessors
import opekope2.optigui.internal.interaction.entityProcessors

object OptiGuiApi : IOptiGuiApi {
    override fun isAvailable(): Boolean = true

    override fun getImplementationModId(): String = "optigui"

    override fun getInteractor(): IInteractor = TextureReplacer

    @Suppress("UNCHECKED_CAST")
    override fun <T : Entity> getEntityProcessor(type: Class<T>): IEntityProcessor<T>? {
        return entityProcessors[type] as IEntityProcessor<T>?
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : BlockEntity> getBlockEntityProcessor(type: Class<T>): IBlockEntityProcessor<T>? {
        return blockEntityProcessors[type] as IBlockEntityProcessor<T>?
    }
}
