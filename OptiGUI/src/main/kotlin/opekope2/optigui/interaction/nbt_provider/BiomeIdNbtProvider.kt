package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.StringTag
import opekope2.optigui.interaction.IInteraction
import java.util.function.Function

/**
 * Provides the biome identifier at a [position][BlockPos].
 *
 * @param blockPosGetter A function that gets the [BlockPos] from the interaction where the biome should be checked
 */
class BiomeIdNbtProvider(private val blockPosGetter: Function<IInteraction, BlockPos>) : IInteractionNbtProvider {
    override fun get(interaction: IInteraction, lookup: HolderLookup.Provider) =
        StringTag.valueOf(interaction.world.getBiome(blockPosGetter.apply(interaction)).registeredName)
}
