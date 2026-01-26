package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.BlockPos
import net.minecraft.core.RegistryAccess
import net.minecraft.world.level.biome.Biome
import opekope2.optigui.interaction.IInteraction
import opekope2.optigui.screen_api.util.NbtUtil
import java.util.function.Function

/**
 * Provides the biome NBT at a [BlockPos].
 *
 * @param blockPosGetter A function that gets the [BlockPos] from the interaction where the biome should be checked
 */
class BiomeNbtProvider(private val blockPosGetter: Function<IInteraction, BlockPos>) : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, registryAccess: RegistryAccess) = NbtUtil.encode(
        interaction.world.getBiome(blockPosGetter.apply(interaction)).value(),
        Biome.DIRECT_CODEC,
        registryAccess
    )
}
