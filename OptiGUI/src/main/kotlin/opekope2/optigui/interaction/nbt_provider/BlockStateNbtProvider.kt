package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.Tag
import net.minecraft.world.level.block.state.BlockState
import opekope2.optigui.interaction.BlockInteraction
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.screen_api.util.NbtUtil

/**
 * Provides the block state NBT of an interaction.
 */
object BlockStateNbtProvider : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, registryAccess: RegistryAccess): Tag? {
        val data = interaction as? BlockInteraction ?: return null
        return NbtUtil.encode(data.blockState, BlockState.CODEC, registryAccess)
    }
}
