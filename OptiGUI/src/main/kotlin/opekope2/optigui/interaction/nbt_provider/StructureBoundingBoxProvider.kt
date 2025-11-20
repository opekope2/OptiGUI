package opekope2.optigui.interaction.nbt_provider

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.Tag
import opekope2.optigui.interaction.IInteraction
import java.util.function.Function

/**
 * Provides the structures at a [BlockPos].
 *
 * @param blockPosGetter A function that gets the [BlockPos] from the interaction where the structures should be checked
 */
class StructureBoundingBoxProvider(private val blockPosGetter: Function<IInteraction, BlockPos>) :
    IInteractionNbtProvider {
    // TODO implement
    override fun get(interaction: IInteraction, lookup: HolderLookup.Provider): Tag? = null
}
