package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.BlockPos
import net.minecraft.core.RegistryAccess
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.screen_api.util.NbtUtil
import java.util.function.Function

/**
 * Provides a [BlockPos] as NBT.
 *
 * @param blockPosGetter A function that gets the [BlockPos] from the interaction
 */
class PositionNbtProvider(private val blockPosGetter: Function<IInteraction, BlockPos>) : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, registryAccess: RegistryAccess) =
        NbtUtil.encode(blockPosGetter.apply(interaction), BlockPos.CODEC, registryAccess)
}
